package ink.pmc.daily.api

import org.bukkit.OfflinePlayer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

interface Daily {

    suspend fun sign(player: OfflinePlayer): DailyHistory

    suspend fun isSigned(date: LocalDate)

    suspend fun isSignedToday(player: OfflinePlayer): Boolean

    suspend fun isSignedBefore(player: OfflinePlayer): Boolean

    suspend fun getHistory(id: UUID): DailyHistory?

    suspend fun getHistory(player: OfflinePlayer, date: LocalDate): DailyHistory?

    suspend fun getTodayHistory(player: OfflinePlayer): DailyHistory?

    suspend fun listHistory(player: OfflinePlayer): Collection<DailyHistory>

    suspend fun getAccumulation(player: OfflinePlayer): Collection<DailyHistory>

    suspend fun getAccumulatedDays(player: OfflinePlayer): Int

    suspend fun getPlayer(id: UUID): DailyPlayer?

    suspend fun getPlayer(player: OfflinePlayer): DailyPlayer?

    suspend fun getLastSignedDate(player: OfflinePlayer): LocalDate?

    suspend fun getLastSignedTime(player: OfflinePlayer): LocalDateTime?

}