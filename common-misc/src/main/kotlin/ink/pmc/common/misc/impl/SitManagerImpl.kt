package ink.pmc.common.misc.impl

import ink.pmc.common.misc.*
import ink.pmc.common.misc.api.sit.SitManager
import ink.pmc.common.misc.api.sit.isSitting
import ink.pmc.common.misc.api.sit.sitter
import ink.pmc.common.misc.api.sit.stand
import ink.pmc.common.utils.concurrent.submitAsync
import ink.pmc.common.utils.concurrent.submitSync
import ink.pmc.common.utils.entity
import ink.pmc.common.utils.player
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

class SitManagerImpl : SitManager {

    private val playerToSitLocationMap = mutableMapOf<UUID, Location>()
    private val playerToSeatMap = mutableMapOf<UUID, UUID>()
    override val sitters: Set<Player>
        get() = playerToSitLocationMap.keys.map { it.player!! }.toSet()
    override val seats: Set<Entity>
        get() = playerToSeatMap.values.map { it.entity!! }.toSet()

    override fun sit(player: Player, location: Location) {
        if (player.isSitting) {
            player.stand()
        }

        var sitLoc = location.rawLocation

        if (!checkLocation(location)) {
            val tryFind = findLegalLocation(sitLoc)

            if (tryFind == null) {
                player.sendMessage(ILLEGAL_LOC)
                return
            }

            sitLoc = tryFind
        }

        if (sitLoc.sitter != null) {
            player.showTitle(MULTI_SITTERS_TITLE)
            player.playSound(MULTI_SITTERS_SOUND)
            return
        }

        val armorStand = createArmorStand(sitLoc)
        markAsSeat(armorStand, player)
        armorStand.addPassenger(player)

        playerToSitLocationMap[player.uniqueId] = sitLoc
        playerToSeatMap[player.uniqueId] = armorStand.uniqueId
        markDelay(player)

        submitAsync {
            delay(45) // 原版载具的提示本身就会延迟发送，防止插件的提示在原版提示之前所以被覆盖掉
            player.sendActionBar(STAND_UP) // 先直接发一次，尝试覆盖原版的载具提示
        }
    }

    override fun isSitting(player: Player): Boolean {
        return playerToSitLocationMap.containsKey(player.uniqueId)
    }

    override fun stand(player: Player) {
        if (!isSitting(player)) {
            return
        }

        val playerId = player.uniqueId
        val armorStandId = playerToSeatMap[playerId]!!
        val armorStand = player.world.getEntity(armorStandId)!!
        val standLocation = player.location.add(0.0, 1.0, 0.0)

        // 使用实体调度器，避免未来在迁移 Folia 时可能造成的问题
        armorStand.submitSync {
            armorStand.removePassenger(player)
        }

        player.teleportAsync(standLocation) // 显式异步传送，同上
        player.sendActionBar(Component.text(" "))

        playerToSitLocationMap.remove(playerId)
        cleanArmorStand(armorStandId)
    }

    override fun getSeat(player: Player): Entity? {
        playerToSeatMap[player.uniqueId] ?: return null
        return plugin.server.getEntity(playerToSeatMap[player.uniqueId]!!)
    }

    override fun getSitLocation(player: Player): Location? {
        return playerToSitLocationMap[player.uniqueId]
    }

    override fun getSitterByLocation(location: Location): Player? {
        return playerToSitLocationMap.entries.firstOrNull { it.value == location.rawLocation }?.key?.player
    }

    override fun getSitterBySeat(seat: Entity): Player? {
        return playerToSeatMap.entries.firstOrNull { it.value == seat.uniqueId }?.key?.player
    }

    override fun isSeat(entity: Entity): Boolean {
        return playerToSeatMap.containsValue(entity.uniqueId)
    }

    override fun isSitLocation(location: Location): Boolean {
        return playerToSitLocationMap.containsValue(location.rawLocation)
    }

    override fun standAll() {
        playerToSitLocationMap.keys.forEach {
            val player = plugin.server.getPlayer(it)
            player!!.stand()
        }
    }

    private fun cleanArmorStand(uuid: UUID) {
        if (!playerToSeatMap.containsValue(uuid)) {
            return
        }

        val armorStand = plugin.server.getEntity(uuid) ?: return
        armorStand.remove()

        playerToSeatMap.entries.removeIf { it.value == uuid }
    }

}