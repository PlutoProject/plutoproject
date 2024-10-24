package ink.pmc.framework.interactive.inventory.placement.offset

import ink.pmc.interactive.api.inventory.components.state.IntOffset
import ink.pmc.framework.interactive.inventory.layout.Placeable

class OffsetPlaceable(
    val offset: IntOffset,
    val inner: Placeable
) : Placeable by inner {
    override fun placeAt(x: Int, y: Int) {
    }
}
