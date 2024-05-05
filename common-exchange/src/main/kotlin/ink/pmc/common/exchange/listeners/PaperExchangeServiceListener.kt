package ink.pmc.common.exchange.listeners

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import ink.pmc.common.exchange.*
import ink.pmc.common.exchange.utils.*
import ink.pmc.common.member.api.paper.member
import ink.pmc.common.utils.platform.paper
import ink.pmc.common.utils.platform.paperUtilsPlugin
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot

@Suppress("UNUSED")
object PaperExchangeServiceListener : Listener {

    @EventHandler
    suspend fun playerQuitEvent(event: PlayerQuitEvent) {
        val player = event.player

        if (!paperExchangeService.isInExchange(player)) {
            return
        }

        paperExchangeService.endExchange(player)
    }

    @EventHandler
    suspend fun playerJoinEvent(event: PlayerJoinEvent) {
        val player = event.player

        if (!hasStatusSnapshot(player)) {
            return
        }

        restoreStatus(player)
        player.member.update()

        paperExchangeService.inExchange.forEach {
            player.hidePlayer(paperUtilsPlugin, paper.getPlayer(it)!!)
        }
    }

    @EventHandler
    suspend fun playerTeleportEvent(event: PlayerTeleportEvent) {
        val player = event.player

        if (!paperExchangeService.isInExchange(player)) {
            return
        }

        paperExchangeService.endExchange(player, false)
    }

    @EventHandler
    suspend fun playerDeathEvent(event: PlayerDeathEvent) {
        val player = event.player

        if (!paperExchangeService.isInExchange(player)) {
            return
        }

        paperExchangeService.endExchange(player, false)
    }

    @EventHandler
    fun playerRecipeDiscoverEvent(event: PlayerRecipeDiscoverEvent) {
        val player = event.player

        if (!paperExchangeService.isInExchange(player)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun playerAdvancementCriterionGrantEvent(event: PlayerAdvancementCriterionGrantEvent) {
        val player = event.player

        if (!paperExchangeService.isInExchange(player)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun inventoryCreativeEvent(event: InventoryCreativeEvent) {
        val player = if (event.whoClicked !is Player) {
            return
        } else {
            event.whoClicked as Player
        }

        if (!paperExchangeService.isInExchange(player)) {
            return
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

        event.cursor = getForbiddenItem(material)
    }

    @EventHandler
    fun blockPlaceEvent(event: BlockPlaceEvent) {
        val player = event.player

        if (!paperExchangeService.isInExchange(player)) {
            return
        }

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun blockBreakEvent(event: BlockBreakEvent) {
        val player = event.player

        if (!paperExchangeService.isInExchange(player)) {
            return
        }

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun playerDropItemEvent(event: PlayerDropItemEvent) {
        val player = event.player

        if (!paperExchangeService.isInExchange(player)) {
            return
        }

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.itemDrop.remove()
    }

    @EventHandler
    suspend fun playerMoveEvent(event: PlayerMoveEvent) {
        val player = event.player

        if (!paperExchangeService.isInExchange(player)) {
            return
        }

        if (event.to.world != exchangeLobby.world) {
            paperExchangeService.endExchange(player, false)
        }
    }

    @EventHandler
    suspend fun playerInteractEvent(event: PlayerInteractEvent) {
        val player = event.player

        if (!paperExchangeService.isInExchange(player)) {
            return
        }

        if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
            return
        }

        event.isCancelled = true

        if (event.hand == EquipmentSlot.HAND
            && event.action == Action.RIGHT_CLICK_BLOCK
            && event.clickedBlock != null
            && isCheckoutSign(event.clickedBlock!!)
        ) {
            paperExchangeService.checkout(player)
        }
    }

}