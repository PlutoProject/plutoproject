package ink.pmc.daily.api

import org.bukkit.OfflinePlayer
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*

interface DailyHistory {

    val id: UUID
    val owner: OfflinePlayer
    val time: ZonedDateTime
    val date: LocalDate

}