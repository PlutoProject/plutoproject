package ink.pmc.interactive.inventory.modifiers.placement.offset

import androidx.compose.runtime.Stable
import ink.pmc.interactive.inventory.components.state.IntOffset
import ink.pmc.interactive.inventory.modifiers.LayoutChangingModifier
import ink.pmc.interactive.inventory.modifiers.Modifier

data class OffsetModifier(
    val offset: IntOffset
) : Modifier.Element<OffsetModifier>, LayoutChangingModifier {
    override fun mergeWith(other: OffsetModifier) = other

    override fun modifyPosition(offset: IntOffset): IntOffset = offset + this.offset
}

@Stable
fun Modifier.offset(x: Int, y: Int) = then(OffsetModifier(IntOffset(x, y)))
