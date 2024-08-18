package ink.pmc.daily.history

import ink.pmc.daily.api.DailyHistory
import ink.pmc.daily.models.HistoryModel
import ink.pmc.utils.player.uuid
import ink.pmc.utils.time.currentZoneId
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*

fun DailyHistoryImpl.toModel(): HistoryModel {
    return HistoryModel(
        id = id.toString(),
        owner = owner.uniqueId.toString(),
        time = time.toInstant().toEpochMilli()
    )
}

class DailyHistoryImpl(model: HistoryModel) : DailyHistory {

    private val instant = Instant.ofEpochMilli(model.time)

    override val id: UUID = model.id.uuid
    override val owner: OfflinePlayer = Bukkit.getOfflinePlayer(model.owner)
    override val time: ZonedDateTime = ZonedDateTime.ofInstant(instant, currentZoneId)
    override val date: LocalDate = time.toLocalDate()

}