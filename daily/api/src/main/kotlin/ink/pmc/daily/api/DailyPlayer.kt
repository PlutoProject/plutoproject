package ink.pmc.daily.api

import org.bukkit.OfflinePlayer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

interface DailyPlayer {

    val id: UUID
    val lastSignTime: ZonedDateTime
    val lastSignDate: LocalDate
    val accumulated: List<UUID>

    fun asBukkit(): OfflinePlayer

}