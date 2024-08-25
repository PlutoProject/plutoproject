package ink.pmc.daily.api

import ink.pmc.utils.inject.inlinedGet
import org.bukkit.OfflinePlayer
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

typealias PostCheckInCallback = (DailyUser) -> Unit

@Suppress("UNUSED")
interface Daily {

    companion object : Daily by inlinedGet()

    suspend fun checkIn(user: UUID): DailyHistory

    suspend fun isCheckedInToday(user: UUID): Boolean

    suspend fun createUser(id: UUID): DailyUser

    suspend fun getUserOrCreate(id: UUID): DailyUser

    suspend fun getUser(id: UUID): DailyUser?

    suspend fun getUser(player: OfflinePlayer): DailyUser?

    suspend fun getHistory(id: UUID): DailyHistory?

    suspend fun listHistory(user: UUID): Collection<DailyHistory>

    suspend fun getHistoryByTime(user: UUID, start: LocalDateTime, end: LocalDateTime): Collection<DailyHistory>

    suspend fun getHistoryByTime(user: UUID, start: Instant, end: Instant): Collection<DailyHistory>

    suspend fun getHistoryByTime(user: UUID, start: Long, end: Long): Collection<DailyHistory>

    suspend fun getHistoryByTime(user: UUID, date: LocalDate): DailyHistory?

    suspend fun getLastCheckIn(user: UUID): LocalDateTime?

    suspend fun getLastCheckInDate(user: UUID): LocalDate?

    suspend fun getAccumulatedDays(user: UUID): Int

    fun registerPostCallback(id: String, block: PostCheckInCallback)

    fun triggerPostCallback(user: DailyUser)

    fun loadHistory(history: DailyHistory)

    fun unloadUser(id: UUID)

    fun shutdown()

}