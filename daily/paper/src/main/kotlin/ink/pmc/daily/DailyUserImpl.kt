package ink.pmc.daily

import ink.pmc.daily.api.DailyUser
import ink.pmc.daily.models.DailyUserModel
import ink.pmc.utils.player.uuid
import ink.pmc.utils.time.currentZoneId
import ink.pmc.utils.time.instant
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.time.LocalDateTime
import java.util.*

class DailyUserImpl(model: DailyUserModel) : DailyUser {

    override val id: UUID = model.id.uuid
    override val player: OfflinePlayer by lazy { Bukkit.getOfflinePlayer(id) }
    override val lastCheckIn: LocalDateTime = LocalDateTime.ofInstant(model.lastCheckIn.instant, currentZoneId)
    override var accumulatedDays: Int = model.accumulatedDays

    override suspend fun checkIn() {
        TODO("Not yet implemented")
    }

    override fun accumulate() {
        accumulatedDays++
    }

}