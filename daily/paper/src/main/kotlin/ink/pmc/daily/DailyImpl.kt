package ink.pmc.daily

import com.github.benmanes.caffeine.cache.Caffeine
import ink.pmc.daily.api.Daily
import ink.pmc.daily.api.DailyHistory
import ink.pmc.daily.api.DailyUser
import ink.pmc.daily.api.PostCheckInCallback
import ink.pmc.daily.models.DailyUserModel
import ink.pmc.daily.repositories.DailyHistoryRepository
import ink.pmc.daily.repositories.DailyUserRepository
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.concurrent.submitAsyncIO
import ink.pmc.utils.player.uuid
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import org.bukkit.OfflinePlayer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

class DailyImpl : Daily, KoinComponent {

    private var isShutdown = false

    private val userRepo by inject<DailyUserRepository>()
    private val historyRepo by inject<DailyHistoryRepository>()

    private val postCheckInCallbacks = mutableMapOf<String, PostCheckInCallback>()
    private val loadedUsers = ConcurrentHashMap<UUID, DailyUser>()
    private val historyCaches = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .buildAsync<UUID, DailyHistory?> { k, _ ->
            submitAsyncIO<DailyHistory?> { loadHistory(k) }.asCompletableFuture()
        }

    init {
        submitAsync {
            while (!isShutdown) {
                delay(10.minutes)
                loadedUsers.entries.removeIf { !it.value.player.isOnline }
            }
        }
    }

    private suspend fun loadUser(id: UUID): DailyUser? {
        val model = userRepo.findById(id) ?: return null
        return DailyUserImpl(model)
    }

    private suspend fun loadHistory(id: UUID): DailyHistory? {
        val model = historyRepo.findById(id) ?: return null
        return DailyHistoryImpl(model)
    }

    override suspend fun checkIn(user: UUID) {
        getUser(user)?.checkIn()
    }

    override suspend fun isCheckedInToday(user: UUID): Boolean {
        return getUser(user)?.isCheckedInToday() ?: false
    }

    override suspend fun createUser(id: UUID): DailyUser {
        require(getUser(id) == null) { "User with id $id already existed" }
        val user = DailyUserModel(
            id = id.toString(),
            lastCheckIn = null,
            accumulatedDays = 0
        )
        userRepo.saveOrUpdate(user)
        return getUser(id) ?: error("Failed to obtain user instance of $id")
    }

    override suspend fun getUserOrCreate(id: UUID): DailyUser {
        return getUser(id) ?: createUser(id)
    }

    override suspend fun getUser(id: UUID): DailyUser? {
        return loadedUsers[id] ?: loadUser(id)?.also { loadedUsers[id] = it }
    }

    override suspend fun getUser(player: OfflinePlayer): DailyUser? {
        return getUser(player.uniqueId)
    }

    override suspend fun getHistory(id: UUID): DailyHistory? {
        return historyCaches.get(id).await()
    }

    override suspend fun listHistory(user: UUID): Collection<DailyHistory> {
        return historyRepo.findByOwner(user).map {
            val uuid = it.id.uuid
            historyCaches.getIfPresent(uuid)?.get() ?: DailyHistoryImpl(it).also { h ->
                historyCaches.put(uuid, CompletableFuture.completedFuture(h))
            }
        }
    }

    override suspend fun getHistoryByTime(user: UUID, start: Instant, end: Instant): Collection<DailyHistory> {
        return historyRepo.findByTime(user, start, end).map {
            val uuid = it.id.uuid
            historyCaches.getIfPresent(uuid)?.get() ?: DailyHistoryImpl(it).also { h ->
                historyCaches.put(uuid, CompletableFuture.completedFuture(h))
            }
        }
    }

    override suspend fun getHistoryByTime(user: UUID, start: Long, end: Long): Collection<DailyHistory> {
        return historyRepo.findByTime(user, start, end).map {
            val uuid = it.id.uuid
            historyCaches.getIfPresent(uuid)?.get() ?: DailyHistoryImpl(it).also { h ->
                historyCaches.put(uuid, CompletableFuture.completedFuture(h))
            }
        }
    }

    override suspend fun getLastCheckIn(user: UUID): LocalDateTime? {
        return getUser(user)?.lastCheckIn
    }

    override suspend fun getLastCheckInDate(user: UUID): LocalDate? {
        return getUser(user)?.lastCheckInDate
    }

    override suspend fun getAccumulatedDays(user: UUID): Int {
        return getUser(user)?.accumulatedDays ?: 0
    }

    override fun registerPostCallback(id: String, block: PostCheckInCallback) {
        postCheckInCallbacks[id] = block
    }

    override fun triggerPostCallback(user: DailyUser) {
        postCheckInCallbacks.values.forEach { it(user) }
    }

    override fun unloadUser(id: UUID) {
        loadedUsers.remove(id)
    }

    override fun shutdown() {
        require(!isShutdown) { "Daily API already shutdown" }
        isShutdown = true
    }

}