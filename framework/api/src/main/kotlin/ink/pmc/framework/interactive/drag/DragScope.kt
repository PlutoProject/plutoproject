package ink.pmc.framework.interactive.drag

import androidx.compose.runtime.Immutable
import ink.pmc.framework.interactive.state.ItemPositions
import org.bukkit.event.inventory.DragType
import org.bukkit.inventory.ItemStack

@Immutable
data class DragScope(
    val dragType: DragType,
    val updatedItems: ItemPositions,
    var cursor: ItemStack?
)
