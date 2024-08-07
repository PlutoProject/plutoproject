package ink.pmc.interactive.inventory.modifiers

import ink.pmc.interactive.inventory.components.state.IntOffset
import ink.pmc.interactive.inventory.components.state.IntSize
import ink.pmc.interactive.inventory.modifiers.Constraints

interface LayoutChangingModifier {
    fun modifyPosition(offset: IntOffset): IntOffset = offset

    /** Modify constraints as they appear to parent nodes laying out this node. */
    fun modifyLayoutConstraints(measuredSize: IntSize, constraints: Constraints): Constraints =
        modifyInnerConstraints(constraints)

    /** Modify constraints as they appear to this node and its children for layout. */
    fun modifyInnerConstraints(constraints: Constraints): Constraints = constraints
}
