package ink.pmc.interactive.api.gui.inventory.modifiers.drag

import androidx.compose.runtime.Immutable
import ink.pmc.interactive.api.gui.inventory.components.state.ItemPositions
import org.bukkit.event.inventory.DragType
import org.bukkit.inventory.ItemStack

@Immutable
data class DragScope(
    val dragType: DragType,
    val updatedItems: ItemPositions,
    var cursor: ItemStack?
)
