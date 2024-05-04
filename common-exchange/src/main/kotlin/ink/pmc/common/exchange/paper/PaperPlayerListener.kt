package ink.pmc.common.exchange.paper

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import ink.pmc.common.exchange.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.player.*

@Suppress("UNUSED")
object PaperPlayerListener : Listener {

    @EventHandler
    suspend fun playerQuitEvent(event: PlayerQuitEvent) {
        handlePlayerQuit(event.player)
    }

    @EventHandler
    suspend fun playerJoinEvent(event: PlayerJoinEvent) {
        handlePlayerJoin(event.player)
    }

    @EventHandler
    suspend fun playerTeleportEvent(event: PlayerTeleportEvent) {
        handlePlayerTeleport(event.player)
    }

    @EventHandler
    suspend fun playerDeathEvent(event: PlayerDeathEvent) {
        handlePlayerDeath(event.entity)
    }

    @EventHandler
    fun playerRecipeDiscoverEvent(event: PlayerRecipeDiscoverEvent) {
        handleRecipeUnlock(event)
    }

    @EventHandler
    fun playerAdvancementCriterionGrantEvent(event: PlayerAdvancementCriterionGrantEvent) {
        handleAdvancementUnlock(event)
    }

    @EventHandler
    fun inventoryCreativeEvent(event: InventoryCreativeEvent) {
        handleInventory(event)
    }

    @EventHandler
    fun blockPlaceEvent(event: BlockPlaceEvent) {
        handleBlockPlace(event)
    }

    @EventHandler
    fun blockBreakEvent(event: BlockBreakEvent) {
        handleBlockBreak(event)
    }

    @EventHandler
    fun playerDropItemEvent(event: PlayerDropItemEvent) {
        handlePlayerDropItem(event)
    }

    @EventHandler
    suspend fun playerMoveEvent(event: PlayerMoveEvent) {
        handlePlayerMove(event)
    }

    @EventHandler
    suspend fun playerInteractEvent(event: PlayerInteractEvent) {
        handlePlayerInteract(event)
    }

}