package ink.pmc.exchange.lobby

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import ink.pmc.exchange.EXCHANGE_BYPASS_PERMISSION
import ink.pmc.exchange.utils.getForbiddenItem
import ink.pmc.exchange.utils.isCheckoutSign
import ink.pmc.exchange.utils.isForbiddenItem
import ink.pmc.exchange.utils.isMaterialAvailable
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerRecipeDiscoverEvent
import org.bukkit.inventory.EquipmentSlot

@Suppress("UNUSED")
object PlayerActionHandler : Listener {

    private fun handleEvent(event: PlayerEvent) {
        if (event !is Cancellable) {
            return
        }

        val player = event.player

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun playerRecipeDiscoverEvent(event: PlayerRecipeDiscoverEvent) {
        handleEvent(event)
    }

    @EventHandler
    fun playerAdvancementCriterionGrantEvent(event: PlayerAdvancementCriterionGrantEvent) {
        handleEvent(event)
    }

    @EventHandler
    fun inventoryCreativeEvent(event: InventoryCreativeEvent) {
        val player = if (event.whoClicked !is Player) {
            return
        } else {
            event.whoClicked as Player
        }

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        val material = event.cursor.type

        if (isForbiddenItem(event.cursor)) {
            return
        }

        if (isMaterialAvailable(material) || material == Material.AIR) {
            return
        }

        event.cursor = getForbiddenItem(material, player)
    }

    @EventHandler
    fun blockPlaceEvent(event: BlockPlaceEvent) {
        val player = event.player

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun blockBreakEvent(event: BlockBreakEvent) {
        val player = event.player

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun playerDropItemEvent(event: PlayerDropItemEvent) {
        val player = event.player

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.itemDrop.remove()
    }

    @EventHandler
    suspend fun playerInteractEvent(event: PlayerInteractEvent) {
        val player = event.player

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true

        if (event.hand == EquipmentSlot.HAND
            && event.action == Action.RIGHT_CLICK_BLOCK
            && event.clickedBlock != null
            && isCheckoutSign(event.clickedBlock!!)
        ) {
            checkout(player)
        }
    }

}