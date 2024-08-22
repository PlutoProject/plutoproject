package ink.pmc.daily

import com.github.benmanes.caffeine.cache.Caffeine
import ink.pmc.daily.api.Daily
import ink.pmc.daily.api.DailyHistory
import ink.pmc.daily.api.DailyUser
import ink.pmc.daily.repositories.DailyHistoryRepository
import ink.pmc.daily.repositories.DailyUserRepository
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.concurrent.submitAsyncIO
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

class DailyImpl : Daily, KoinComponent {

    private var isShutdown = false

    private val userRepo by inject<DailyUserRepository>()
    private val historyRepo by inject<DailyHistoryRepository>()

    private val userMap = ConcurrentHashMap<UUID, DailyUser>()
    private val historyCaches = Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .buildAsync<UUID, DailyHistory?> { k, _ ->
            submitAsyncIO<DailyHistory?> { loadHistory(k) }.asCompletableFuture()
        }

    init {
        submitAsync {
            while (!isShutdown) {
                delay(10.minutes)
                userMap.entries.removeIf { !it.value.player.isOnline }
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
        TODO("Not yet implemented")
    }

    override suspend fun didCheckInToDay(user: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(id: UUID): DailyUser? {
        return userMap[id] ?: loadUser(id)?.also { userMap[id] = it }
    }

    override suspend fun getUser(player: OfflinePlayer): DailyUser? {
        return getUser(player.uniqueId)
    }

    override suspend fun getHistory(id: UUID): DailyHistory? {
        return historyCaches.get(id).await()
    }

    override suspend fun listHistory(user: UUID): Collection<DailyHistory> {
        TODO("Not yet implemented")
    }

    override suspend fun getHistoryByTime(user: UUID, start: Instant, end: Instant): Collection<DailyHistory> {
        TODO("Not yet implemented")
    }

    override suspend fun getHistoryByTime(user: UUID, start: Long, end: Long): Collection<DailyHistory> {
        TODO("Not yet implemented")
    }

    override suspend fun getLastCheckIn(user: UUID): LocalDateTime? {
        TODO("Not yet implemented")
    }

    override suspend fun getLastCheckInDate(user: UUID): LocalDate? {
        TODO("Not yet implemented")
    }

    override suspend fun getAccumulatedDays(user: UUID): Int {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        require(!isShutdown) { "Daily API already shutdown" }
        isShutdown = true
    }

}