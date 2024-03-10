package ink.pmc.common.misc.listeners

import ink.pmc.common.misc.impl.*
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityDismountEvent
import org.bukkit.event.entity.EntityTeleportEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent

@Suppress("UNUSED", "UNUSED_PARAMETER")
object PlayerListener : Listener {

    @EventHandler
    fun playerQuitEvent(event: PlayerQuitEvent) {
        processPlayerQuit(event)
    }

    @EventHandler
    fun entityDismountEvent(event: EntityDismountEvent) {
        processSitDelay(event)
    }

    @EventHandler
    fun entityDeathEvent(event: EntityDeathEvent) {
        tryToStand(event, false)
        processArmorStandAction(event)
    }

    @EventHandler
    fun entityDamageEvent(event: EntityDamageEvent) {
        tryToStand(event, false)
        processArmorStandAction(event)
    }

    @EventHandler
    fun entityTeleportEvent(event: EntityTeleportEvent) {
        tryToStand(event, false)
        processArmorStandAction(event)
    }

    @EventHandler
    fun entityMoveEvent(event: EntityMoveEvent) {
        // tryToStand(event, false) 暂时移除，可能有问题
        processArmorStandAction(event)
    }

    @EventHandler
    fun blockBreakEvent(event: BlockBreakEvent) {
        processSitLocationBroke(event)
    }

    @EventHandler
    fun playerInteractEvent(event: PlayerInteractEvent) {
        handleSitClick(event)
    }

    @EventHandler
    fun chunkLoadEvent(event: ChunkLoadEvent) {
        processArmorStandClear(event)
    }

}