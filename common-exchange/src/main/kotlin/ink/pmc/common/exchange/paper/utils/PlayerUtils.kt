package ink.pmc.common.exchange.paper.utils

import ink.pmc.common.exchange.CHECKOUT_OVER_SIZE
import ink.pmc.common.exchange.STATUS_SNAPSHOT_KEY
import ink.pmc.common.exchange.paper.StatusSnapshot
import ink.pmc.common.member.api.paper.member
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.concurrent.submitAsync
import ink.pmc.common.utils.visual.mochaFlamingo
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun distributeItems(player: Player, items: List<ItemStack>) {
    val mutable = items as MutableList
    val remaining = getRemainingSpace(player)
    val shouldDrop = mutableListOf<ItemStack>()
    val over = if (items.size < remaining) {
        val amount = remaining - items.size
        shouldDrop.addAll(mutable.subList(0, amount - 1))
        amount
    } else {
        0
    }

    mutable.removeAll(shouldDrop)

    player.inventory.addItem(*mutable.toTypedArray())
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
    submitAsync { member.update() }
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
    submitAsync { member.update() }
}

fun clearInventory(player: Player) {
    player.inventory.clear()
}
