package ink.pmc.daily

import ink.pmc.daily.api.DailyHistory
import ink.pmc.daily.models.DailyHistoryModel
import ink.pmc.utils.player.uuid
import ink.pmc.utils.time.instant
import ink.pmc.utils.time.utcZoneId
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.time.LocalDateTime
import java.util.*

class DailyHistoryImpl(model: DailyHistoryModel) : DailyHistory {

    override val id: UUID = model.id.uuid
    override val owner: UUID = model.owner.uuid
    override val ownerPlayer: OfflinePlayer by lazy { Bukkit.getOfflinePlayer(owner) }
    override val createdAt: LocalDateTime = LocalDateTime.ofInstant(model.createdAt.instant, utcZoneId)

}