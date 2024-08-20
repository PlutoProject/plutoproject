package ink.pmc.daily.api

import org.bukkit.OfflinePlayer
import java.time.LocalDateTime
import java.util.*

interface DailyHistory {

    val id: UUID
    val owner: UUID
    val ownerPlayer: OfflinePlayer
    val createdAt: LocalDateTime

}