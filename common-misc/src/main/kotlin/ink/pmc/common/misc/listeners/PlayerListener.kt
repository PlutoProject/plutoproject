package ink.pmc.common.misc.listeners

import ink.pmc.common.misc.api.isSitting
import ink.pmc.common.misc.api.stand
import ink.pmc.common.misc.impl.handleSitClick
import ink.pmc.common.misc.impl.sitDelay
import ink.pmc.common.misc.impl.tryToStand
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityDismountEvent
import org.bukkit.event.entity.EntityTeleportEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UNUSED", "UNUSED_PARAMETER")
object PlayerListener : Listener {

    @EventHandler
    fun playerQuitEvent(event: PlayerQuitEvent) {
        val player = event.player

        if (player.isSitting) {
            player.stand()
        }
    }

    @EventHandler
    fun entityDismountEvent(event: EntityDismountEvent) {
        if (!sitDelay.contains(event.entity.uniqueId)) {
            tryToStand(event)
        } else {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun entityDeathEvent(event: EntityDeathEvent) {
        tryToStand(event, false)
    }

    @EventHandler
    fun entityDamageEvent(event: EntityDamageEvent) {
        tryToStand(event, false)
    }

    @EventHandler
    fun entityTeleportEvent(event: EntityTeleportEvent) {
        tryToStand(event, false)
    }

    @EventHandler
    fun entityMoveEvent(event: EntityMoveEvent) {
        // tryToStand(event, false) 暂时移除，可能有问题
    }

    @EventHandler
    fun playerInteractEvent(event: PlayerInteractEvent) {
        handleSitClick(event)
    }

}