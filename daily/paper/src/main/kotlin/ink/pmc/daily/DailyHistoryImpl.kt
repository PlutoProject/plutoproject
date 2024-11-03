package ink.pmc.daily

import ink.pmc.daily.api.DailyHistory
import ink.pmc.daily.models.DailyHistoryModel
import ink.pmc.framework.utils.player.uuid
import ink.pmc.framework.utils.time.currentZoneId
import ink.pmc.framework.utils.time.instant
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class DailyHistoryImpl(model: DailyHistoryModel) : DailyHistory {

    override val id: UUID = model.id.uuid
    override val owner: UUID = model.owner.uuid
    override val ownerPlayer: OfflinePlayer by lazy { Bukkit.getOfflinePlayer(owner) }
    override val createdAt: LocalDateTime = LocalDateTime.ofInstant(model.createdAt.instant, currentZoneId)
    override val createdDate: LocalDate = createdAt.toLocalDate()
    override val rewarded: Double = model.rewarded

}