package ink.pmc.interactive.inventory

import ink.pmc.interactive.api.inventory.canvas.GuiInventoryHolder
import ink.pmc.interactive.api.inventory.modifiers.click.ClickScope
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

@Suppress("UNUSED", "UnusedReceiverParameter")
object InventoryListener : Listener {

    @EventHandler
    fun InventoryClickEvent.e() {
        val invHolder = inventory.holder as? GuiInventoryHolder ?: return

        // Avoid any exploits shift clicking or double-clicking into/from the GUI
        if (click !in setOf(LEFT, RIGHT, MIDDLE)) isCancelled = true

        val clickedInventory = clickedInventory ?: return
        if (clickedInventory.holder !== invHolder) return
        isCancelled = true

        val scope = ClickScope(
            click, slot, cursor.takeIf { it.type != Material.AIR }, whoClicked
        )
        invHolder.processClick(scope, this)
    }

    @EventHandler
    fun InventoryCloseEvent.e() {
        val invHolder = inventory.holder as? GuiInventoryHolder ?: return
        val scope = invHolder.scope
        if (!scope.isPendingRefresh.value) {
            invHolder.onClose(player as Player)
            scope.dispose()
            return
        }
        scope.isPendingRefresh.value = false
    }

    @EventHandler
    fun InventoryDragEvent.e() {
        val invHolder = inventory.holder as? GuiInventoryHolder ?: return
        val inInv = newItems.filter { it.key < view.topInventory.size }
        if (newItems.size == 1 && inInv.size == 1) {
            isCancelled = true
            val clicked = inInv.entries.first()
            val scope = ClickScope(LEFT, clicked.key, cursor?.takeIf { it.type != Material.AIR }, whoClicked)
            invHolder.processClick(scope, this)
        } else if (inInv.isNotEmpty()) {
            isCancelled = true
        }
    }

}