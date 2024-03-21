package ink.pmc.common.misc

import io.papermc.paper.event.entity.EntityMoveEvent
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent

@Suppress("UNUSED", "UNUSED_PARAMETER")
object Listeners : Listener {

    @EventHandler
    fun playerQuitEvent(event: PlayerQuitEvent) {
        handlePlayerQuit(event)
        handleQuitMessage(event)
    }

    @EventHandler
    fun entityDismountEvent(event: EntityDismountEvent) {
        handleSitDelay(event)
    }

    @EventHandler
    fun entityDeathEvent(event: EntityDeathEvent) {
        tryToStand(event, false)
        handleArmorStandAction(event)
    }

    @EventHandler
    fun entityDamageEvent(event: EntityDamageEvent) {
        tryToStand(event, false)
        handleArmorStandAction(event)
    }

    @EventHandler
    fun entityTeleportEvent(event: EntityTeleportEvent) {
        tryToStand(event, false)
        handleArmorStandAction(event)
    }

    @EventHandler
    fun entityMoveEvent(event: EntityMoveEvent) {
        // tryToStand(event, false) 暂时移除，可能有问题
        handleArmorStandAction(event)
    }

    @EventHandler
    fun blockBreakEvent(event: BlockBreakEvent) {
        handleSitLocationBroke(event)
    }

    @EventHandler
    fun blockBreakEvent(event: BlockBurnEvent) {
        handleSitLocationBroke(event)
    }

    @EventHandler
    fun blockExplodeEvent(event: BlockExplodeEvent) {
        handleSitLocationBroke(event)
    }

    @EventHandler
    fun blockFadeEvent(event: BlockFadeEvent) {
        handleSitLocationBroke(event)
    }

    @EventHandler
    fun blockIgniteEvent(event: BlockIgniteEvent) {
        handleSitLocationBroke(event)
    }

    @EventHandler
    fun entityExplodeEvent(event: EntityExplodeEvent) {
        handleSitLocationBroke(event)
    }

    @EventHandler
    fun playerInteractEvent(event: PlayerInteractEvent) {
        handleSitClick(event)
    }

    @EventHandler
    fun blockPistonExtendEvent(event: BlockPistonExtendEvent) {
        handleSitLocationBroke(event)
    }

    @EventHandler
    fun blockPistonRetractEvent(event: BlockPistonRetractEvent) {
        handleSitLocationBroke(event)
    }

    @EventHandler
    fun entitySpawnEvent(event: EntitySpawnEvent) {
        handleSitLocationBroke(event)
    }

    @EventHandler
    fun leavesDecayEvent(event: LeavesDecayEvent) {
        handleSitLocationBroke(event)
    }

    @EventHandler
    fun chunkLoadEvent(event: ChunkLoadEvent) {

    }

    @EventHandler
    fun asyncChatEvent(event: AsyncChatEvent) {
        handleChatFormat(event)
    }

    @EventHandler
    fun playerJoinEvent(event: PlayerJoinEvent) {
        handleJoinMessage(event)
    }

}