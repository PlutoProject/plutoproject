package ink.pmc.daily.api

import org.bukkit.OfflinePlayer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

interface DailyHistory {

    val id: UUID
    val owner: OfflinePlayer
    val date: LocalDate
    val time: LocalDateTime

}