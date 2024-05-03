package ink.pmc.common.exchange

import ink.pmc.common.exchange.paper.utils.hasStatusSnapshot
import ink.pmc.common.exchange.paper.utils.restoreStatus
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.platform.paperUtilsPlugin
import ink.pmc.common.utils.visual.mochaFlamingo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.player.PlayerRecipeDiscoverEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun handlePlayerQuit(player: Player) {
    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    paperExchangeService.endExchange(player)
}

fun handlePlayerJoin(player: Player) {
    if (!hasStatusSnapshot(player)) {
        return
    }

    restoreStatus(player)
}

fun handlePlayerTeleport(player: Player) {
    if (!paperExchangeService.isInExchange(player)) {
        return
    }

    paperExchangeService.endExchange(player, false)
}

fun handlePlayerDeath(player: Player) {
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

@Suppress("UNUSED")
val bypassedActions = listOf(
    InventoryAction.PLACE_ALL,
    InventoryAction.PLACE_SOME,
    InventoryAction.PLACE_ONE,
    InventoryAction.DROP_ALL_SLOT,
    InventoryAction.DROP_ONE_SLOT,
    InventoryAction.DROP_ALL_CURSOR,
    InventoryAction.DROP_ONE_CURSOR,
    InventoryAction.SWAP_WITH_CURSOR,
    InventoryAction.HOTBAR_SWAP,
    InventoryAction.HOTBAR_MOVE_AND_READD,
    InventoryAction.PICKUP_ALL,
    InventoryAction.PICKUP_ONE,
    InventoryAction.PICKUP_HALF,
    InventoryAction.PICKUP_SOME,
)

fun handleInventory(event: InventoryCreativeEvent) {
    if (event.whoClicked.hasPermission("exchange.bypass")) {
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

fun isMaterialAvailable(material: Material): Boolean {
    return ExchangeConfig.AvailableItems.materials.contains(material)
}

val forbiddenItemDataKey = NamespacedKey(paperUtilsPlugin, "forbidden_item")

fun isForbiddenItem(item: ItemStack): Boolean {
    if (item.itemMeta == null) {
        return false
    }

    return item.itemMeta.persistentDataContainer.has(forbiddenItemDataKey)
}

fun getForbiddenItem(material: Material): ItemStack {
    return ItemStack(Material.BARRIER, 1).apply {
        editMeta {
            it.displayName(
                MATERIAL_NOT_AVAILABLE_NAME
                    .replace("<material>", Component.translatable(material).color(mochaFlamingo))
                    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            )
            it.lore(MATERIAL_NOT_AVAILABLE_LORE.map { component ->
                component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            })
            it.persistentDataContainer.set(forbiddenItemDataKey, PersistentDataType.STRING, material.toString())
        }
    }
}