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

    suspend fun checkIn(user: UUID)

    suspend fun isCheckedInToday(user: UUID): Boolean

    suspend fun getUser(id: UUID): DailyUser?

    suspend fun getUser(player: OfflinePlayer): DailyUser?

    suspend fun getHistory(id: UUID): DailyHistory?

    suspend fun listHistory(user: UUID): Collection<DailyHistory>

    suspend fun getHistoryByTime(user: UUID, start: Instant, end: Instant): Collection<DailyHistory>

    suspend fun getHistoryByTime(user: UUID, start: Long, end: Long): Collection<DailyHistory>

    suspend fun getLastCheckIn(user: UUID): LocalDateTime?

    suspend fun getLastCheckInDate(user: UUID): LocalDate?

    suspend fun getAccumulatedDays(user: UUID): Int

    fun registerPostCallback(id: String, block: PostCheckInCallback)

    fun triggerPostCallback(user: DailyUser)

    fun shutdown()

}