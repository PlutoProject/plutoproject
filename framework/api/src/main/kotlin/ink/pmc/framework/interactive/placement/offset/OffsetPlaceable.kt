package ink.pmc.framework.interactive.placement.offset

import ink.pmc.framework.interactive.layout.Placeable
import ink.pmc.framework.interactive.state.IntOffset

class OffsetPlaceable(
    val offset: IntOffset,
    val inner: Placeable
) : Placeable by inner {
    override fun placeAt(x: Int, y: Int) {
    }
}
