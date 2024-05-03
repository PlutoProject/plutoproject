package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRecipeDiscoverEvent
import org.bukkit.event.player.PlayerTeleportEvent

@Suppress("UNUSED")
object PaperPlayerListener : Listener {

    @EventHandler
    fun playerQuitEvent(event: PlayerQuitEvent) {
        handlePlayerQuit(event.player)
    }

    @EventHandler
    fun playerJoinEvent(event: PlayerJoinEvent) {
        handlePlayerJoin(event.player)
    }

    @EventHandler
    fun playerTeleportEvent(event: PlayerTeleportEvent) {
        handlePlayerTeleport(event.player)
    }

    @EventHandler
    fun playerDeathEvent(event: PlayerDeathEvent) {
        handlePlayerDeath(event.entity)
    }

    @EventHandler
    fun playerRecipeDiscoverEvent(event: PlayerRecipeDiscoverEvent) {
        handleRecipeUnlock(event)
    }

    @EventHandler
    fun inventoryCreativeEvent(event: InventoryCreativeEvent) {
        handleInventory(event)
    }

}