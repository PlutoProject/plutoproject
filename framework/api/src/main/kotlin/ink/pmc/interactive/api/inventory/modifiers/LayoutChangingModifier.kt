package ink.pmc.interactive.api.inventory.modifiers

import ink.pmc.interactive.api.inventory.components.state.IntOffset
import ink.pmc.interactive.api.inventory.components.state.IntSize

interface LayoutChangingModifier {
    fun modifyPosition(offset: IntOffset): IntOffset = offset

    /** Modify constraints as they appear to parent nodes laying out this builder. */
    fun modifyLayoutConstraints(measuredSize: IntSize, constraints: Constraints): Constraints =
        modifyInnerConstraints(constraints)

    /** Modify constraints as they appear to this builder and its children for layout. */
    fun modifyInnerConstraints(constraints: Constraints): Constraints = constraints
}
