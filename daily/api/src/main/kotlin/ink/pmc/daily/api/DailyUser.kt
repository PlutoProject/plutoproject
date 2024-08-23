package ink.pmc.daily.api

import org.bukkit.OfflinePlayer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

interface DailyUser {

    val id: UUID
    val player: OfflinePlayer
    val lastCheckIn: LocalDateTime?
    val lastCheckInDate: LocalDate?
    val accumulatedDays: Int

    suspend fun checkIn()

    suspend fun clearAccumulation()

    suspend fun resetCheckInTime()

    suspend fun isCheckedInToday(): Boolean

    suspend fun isCheckedInYesterday(): Boolean

    suspend fun update()

}