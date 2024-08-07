package ink.pmc.interactive.inventory.modifiers.placement.offset

import ink.pmc.interactive.inventory.components.state.IntOffset
import ink.pmc.interactive.inventory.layout.Placeable

class OffsetPlaceable(
    val offset: IntOffset,
    val inner: Placeable
) : Placeable by inner {
    override fun placeAt(x: Int, y: Int) {
    }
}
