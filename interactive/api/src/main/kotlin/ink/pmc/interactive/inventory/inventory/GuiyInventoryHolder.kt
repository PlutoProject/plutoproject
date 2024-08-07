package ink.pmc.interactive.inventory.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ink.pmc.interactive.inventory.modifiers.click.ClickScope
import ink.pmc.interactive.inventory.modifiers.drag.DragScope
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class GuiyInventoryHolder : InventoryHolder {

    var activeInventory: Inventory? by mutableStateOf(null)

    override fun getInventory(): Inventory =
        activeInventory ?: error("Guiy inventory is used in bukkit but has not been rendered yet.")

    abstract fun processClick(scope: ClickScope, event: Cancellable)
    abstract fun processDrag(scope: DragScope)

    abstract fun onClose(player: Player)

    fun close() {
        inventory.viewers.forEach { it.closeInventory(InventoryCloseEvent.Reason.PLUGIN) }
    }

}
