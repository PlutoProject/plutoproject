package ink.pmc.daily

import ink.pmc.daily.api.Daily
import ink.pmc.daily.api.DailyHistory
import ink.pmc.daily.api.DailyUser
import org.bukkit.OfflinePlayer
import org.koin.core.component.KoinComponent
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class DailyImpl : Daily, KoinComponent {

    override suspend fun checkIn(user: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun didCheckInToDay(user: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(id: UUID): DailyUser? {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(player: OfflinePlayer): DailyUser? {
        TODO("Not yet implemented")
    }

    override suspend fun getHistory(id: UUID): DailyHistory? {
        TODO("Not yet implemented")
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

}