package ink.pmc.interactive.api.inventory.canvas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ink.pmc.interactive.api.GuiInventoryScope
import ink.pmc.interactive.api.inventory.modifiers.click.ClickScope
import ink.pmc.interactive.api.inventory.modifiers.drag.DragScope
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class GuiInventoryHolder(val scope: GuiInventoryScope) : InventoryHolder {

    var activeInventory: Inventory? by mutableStateOf(null)

    override fun getInventory(): Inventory =
        activeInventory ?: error("Interactive inventory is used in bukkit but has not been rendered yet.")

    abstract suspend fun processClick(scope: ClickScope, event: Cancellable)
    abstract suspend fun processDrag(scope: DragScope)

    abstract fun onClose(player: Player)

    fun close() {
        inventory.viewers.forEach { it.closeInventory(InventoryCloseEvent.Reason.PLUGIN) }
    }

}