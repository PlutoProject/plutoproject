package ink.pmc.daily

import ink.pmc.daily.api.Daily
import ink.pmc.daily.api.DailyHistory
import ink.pmc.daily.api.DailyPlayer
import org.bukkit.OfflinePlayer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class DailyImpl : Daily, KoinComponent {

    private val conf by inject<DailyConfig>()

    override suspend fun sign(player: OfflinePlayer): DailyHistory {
        TODO("Not yet implemented")
    }

    override suspend fun isSigned(date: LocalDate) {
        TODO("Not yet implemented")
    }

    override suspend fun isSignedToday(player: OfflinePlayer): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isSignedBefore(player: OfflinePlayer): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getHistory(id: UUID): DailyHistory? {
        TODO("Not yet implemented")
    }

    override suspend fun getHistory(player: OfflinePlayer, date: LocalDate): DailyHistory? {
        TODO("Not yet implemented")
    }

    override suspend fun getTodayHistory(player: OfflinePlayer): DailyHistory? {
        TODO("Not yet implemented")
    }

    override suspend fun listHistory(player: OfflinePlayer): Collection<DailyHistory> {
        TODO("Not yet implemented")
    }

    override suspend fun getAccumulation(player: OfflinePlayer): Collection<DailyHistory> {
        TODO("Not yet implemented")
    }

    override suspend fun getAccumulatedDays(player: OfflinePlayer): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayer(id: UUID): DailyPlayer? {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayer(player: OfflinePlayer): DailyPlayer? {
        TODO("Not yet implemented")
    }

    override suspend fun getLastSignedDate(player: OfflinePlayer): LocalDate? {
        TODO("Not yet implemented")
    }

    override suspend fun getLastSignedTime(player: OfflinePlayer): LocalDateTime? {
        TODO("Not yet implemented")
    }

}