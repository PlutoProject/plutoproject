package ink.pmc.interactive.api.inventory.modifiers

import ink.pmc.interactive.api.inventory.components.state.IntOffset
import ink.pmc.interactive.api.inventory.components.state.IntSize
import ink.pmc.interactive.api.inventory.modifiers.Constraints

interface LayoutChangingModifier {
    fun modifyPosition(offset: IntOffset): IntOffset = offset

    /** Modify constraints as they appear to parent nodes laying out this applier. */
    fun modifyLayoutConstraints(measuredSize: IntSize, constraints: Constraints): Constraints =
        modifyInnerConstraints(constraints)

    /** Modify constraints as they appear to this applier and its children for layout. */
    fun modifyInnerConstraints(constraints: Constraints): Constraints = constraints
}
