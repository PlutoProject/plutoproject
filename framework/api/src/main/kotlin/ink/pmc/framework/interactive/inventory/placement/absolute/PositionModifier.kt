package ink.pmc.framework.interactive.inventory.placement.absolute

import ink.pmc.interactive.api.inventory.components.state.IntOffset
import ink.pmc.framework.interactive.inventory.LayoutChangingModifier
import ink.pmc.framework.interactive.inventory.Modifier

class PositionModifier(
    val x: Int = 0,
    val y: Int = 0,
) : Modifier.Element<PositionModifier>, LayoutChangingModifier {
    override fun mergeWith(other: PositionModifier) = other

    override fun modifyPosition(offset: IntOffset) = IntOffset(this.x, this.y)
}

/** Places an element at an absolute offset in the inventory. */
fun Modifier.at(x: Int = 0, y: Int = 0) = then(PositionModifier(x, y))
