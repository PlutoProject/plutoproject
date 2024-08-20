package ink.pmc.daily.api

import org.bukkit.OfflinePlayer
import java.time.LocalDateTime
import java.util.*

interface DailyUser {

    val id: UUID
    val player: OfflinePlayer
    val lastCheckIn: LocalDateTime
    val accumulatedDays: Int

    suspend fun checkIn()

    fun accumulate()

}