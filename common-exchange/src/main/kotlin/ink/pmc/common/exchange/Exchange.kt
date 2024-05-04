package ink.pmc.common.exchange

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import ink.pmc.common.exchange.extensions.noLessThan
import ink.pmc.common.exchange.paper.utils.*
import ink.pmc.common.member.api.paper.member
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.platform.paper
import ink.pmc.common.utils.platform.paperUtilsPlugin
import ink.pmc.common.utils.visual.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRecipeDiscoverEvent
import org.bukkit.inventory.EquipmentSlot

suspend fun handlePlayerQuit(player: Player) {
    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    paperExchangeService.endExchange(player)
}

suspend fun handlePlayerJoin(player: Player) {
    if (!hasStatusSnapshot(player)) {
        return
    }

    restoreStatus(player)

    paperExchangeService.inExchange.forEach {
        player.hidePlayer(paperUtilsPlugin, paper.getPlayer(it)!!)
    }
}

suspend fun handlePlayerTeleport(player: Player) {
    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    paperExchangeService.endExchange(player, false)
}

suspend fun handlePlayerMove(event: PlayerMoveEvent) {
    val player = event.player

    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    if (event.to.world != exchangeLobby.world) {
        paperExchangeService.endExchange(player, false)
    }
}

fun handleBlockPlace(event: BlockPlaceEvent) {
    val player = event.player

    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
        return
    }

    event.isCancelled = true
}

fun handleBlockBreak(event: BlockBreakEvent) {
    val player = event.player

    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
        return
    }

    event.isCancelled = true
}

fun handlePlayerDropItem(event: PlayerDropItemEvent) {
    val player = event.player

    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    if (player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
        return
    }

    event.itemDrop.remove()
}

suspend fun handlePlayerDeath(player: Player) {
    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    paperExchangeService.endExchange(player, false)
}

fun handleRecipeUnlock(event: PlayerRecipeDiscoverEvent) {
    val player = event.player

    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    event.isCancelled = true
}

fun handleAdvancementUnlock(event: PlayerAdvancementCriterionGrantEvent) {
    val player = event.player

    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    event.isCancelled = true
}

suspend fun checkout(player: Player) {
    paperExchangeService.checkout(player)
}

suspend fun handlePlayerInteract(event: PlayerInteractEvent) {
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
        checkout(player)
    }
}

fun handleInventory(event: InventoryCreativeEvent) {
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

    /*if (event.action in bypassedActions) {
            return
    }*/

    event.cursor = getForbiddenItem(material)
}