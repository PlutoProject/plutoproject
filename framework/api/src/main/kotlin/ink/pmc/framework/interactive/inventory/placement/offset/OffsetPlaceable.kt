package ink.pmc.framework.interactive.inventory.placement.offset

import ink.pmc.framework.interactive.inventory.layout.Placeable
import ink.pmc.framework.interactive.inventory.state.IntOffset

class OffsetPlaceable(
    val offset: IntOffset,
    val inner: Placeable
) : Placeable by inner {
    override fun placeAt(x: Int, y: Int) {
    }
}
