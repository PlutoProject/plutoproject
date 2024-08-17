package ink.pmc.daily.api

import org.bukkit.OfflinePlayer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

interface DailyPlayer {

    val id: UUID
    val lastSignDate: LocalDate
    val lastSignTime: LocalDateTime
    val monthAccumulated: List<UUID>

    fun asBukkit(): OfflinePlayer

}