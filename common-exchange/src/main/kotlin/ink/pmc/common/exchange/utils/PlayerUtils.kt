package ink.pmc.common.exchange.utils

import ink.pmc.common.exchange.*
import ink.pmc.common.exchange.paper.StatusSnapshot
import ink.pmc.common.member.api.paper.member
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.platform.paper
import ink.pmc.common.utils.platform.paperUtilsPlugin
import ink.pmc.common.utils.visual.mochaFlamingo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun distributeItems(player: Player, items: List<ItemStack>) {
    val mutable = items as MutableList
    val remaining = getRemainingSpace(player)
    val shouldDrop = mutableListOf<ItemStack>()
    val over = if (remaining < items.size) {
        val amount = items.size - remaining
        shouldDrop.addAll(mutable.subList(0, amount))
        amount
    } else {
        0
    }

    mutable.removeAll(shouldDrop)
    player.inventory.addItem(*mutable.toTypedArray())

    if (shouldDrop.size <= 0) {
        return
    }

    shouldDrop.forEach {
        player.world.dropItem(player.location, it)
    }

    player.sendMessage(
        CHECKOUT_OVER_SIZE
            .replace("<amount>", Component.text(over).color(mochaFlamingo))
    )
}

fun getRemainingSpace(player: Player): Int {
    val inventory: Inventory = player.inventory
    var remainingSpace = 0

    for (i in 0..35) {
        val itemStack = inventory.getItem(i)
        if (itemStack == null || itemStack.type.isAir) {
            remainingSpace++
        }
    }

    return remainingSpace
}

fun hasStatusSnapshot(player: Player): Boolean {
    val member = player.member
    val dataContainer = member.dataContainer
    return dataContainer.contains(STATUS_SNAPSHOT_KEY)
}

fun snapshotStatus(player: Player) {
    val member = player.member
    val dataContainer = member.dataContainer

    if (hasStatusSnapshot(player)) {
        return
    }

    val snapshot = StatusSnapshot.create(player)
    dataContainer[STATUS_SNAPSHOT_KEY] = snapshot
}

fun restoreStatus(player: Player, restoreLocation: Boolean = true) {
    val member = player.member
    val dataContainer = member.dataContainer

    if (!hasStatusSnapshot(player)) {
        return
    }

    val snapshot = dataContainer[STATUS_SNAPSHOT_KEY, StatusSnapshot::class.java]!!
    snapshot.restore(player, restoreLocation)
    dataContainer.remove(STATUS_SNAPSHOT_KEY)
}

fun clearInventory(player: Player) {
    player.inventory.clear()
}

fun isMaterialAvailable(material: Material): Boolean {
    return ExchangeConfig.AvailableItems.materials.contains(material)
}

private val forbiddenItemDataKey = NamespacedKey(paperUtilsPlugin, "forbidden_item")

fun isForbiddenItem(item: ItemStack): Boolean {
    if (item.itemMeta == null) {
        return false
    }

    return item.itemMeta.persistentDataContainer.has(forbiddenItemDataKey)
}

fun getForbiddenItem(material: Material): ItemStack {
    return ItemStack(material, 1).apply {
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

fun applyExchangeStatus(player: Player) {
    player.gameMode = GameMode.CREATIVE
    if (!player.hasPermission(EXCHANGE_BYPASS_PERMISSION)) {
        player.allowFlight = false
    }
    player.isFlying = false
    hidePlayer(player)
    clearInventory(player)
    player.clearActivePotionEffects()
}

fun hidePlayer(player: Player) {
    paper.onlinePlayers
        .filter { it != player && !it.hasPermission(EXCHANGE_BYPASS_PERMISSION) }
        .forEach {
            it.hidePlayer(paperUtilsPlugin, player)
        }
}

fun showPlayer(player: Player) {
    paper.onlinePlayers
        .filter { it != player }
        .forEach {
            it.showPlayer(paperUtilsPlugin, player)
        }
}

fun cart(player: Player): List<ItemStack> {
    return player.inventory.contents.clone().filter {
        it != null && (!isForbiddenItem(it) || isMaterialAvailable(it.type))
    }.map { it!! }
}