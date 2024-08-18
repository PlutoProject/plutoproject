package ink.pmc.daily.player

import ink.pmc.daily.api.DailyPlayer
import ink.pmc.daily.models.PlayerModel
import ink.pmc.utils.player.uuid
import ink.pmc.utils.time.currentZoneId
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*

fun DailyPlayer.toModel(): PlayerModel {
    return PlayerModel(
        id = id.toString(),
        lastSign = lastSignTime.toInstant().toEpochMilli(),
        accumulated = accumulated.map { it.toString() }
    )
}

class DailyPlayerImpl(model: PlayerModel) : DailyPlayer {

    private val instant = Instant.ofEpochMilli(model.lastSign)

    override val id: UUID = model.id.uuid
    override val lastSignTime: ZonedDateTime = ZonedDateTime.ofInstant(instant, currentZoneId)
    override val lastSignDate: LocalDate = lastSignTime.toLocalDate()
    override val accumulated: List<UUID> = model.accumulated.map { it.uuid }

    override fun asBukkit(): OfflinePlayer {
        return Bukkit.getOfflinePlayer(id)
    }

}