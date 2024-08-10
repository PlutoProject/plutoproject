package ink.pmc.interactive.api.inventory.modifiers.placement.offset

import androidx.compose.runtime.Stable
import ink.pmc.interactive.api.inventory.components.state.IntOffset
import ink.pmc.interactive.api.inventory.modifiers.LayoutChangingModifier
import ink.pmc.interactive.api.inventory.modifiers.Modifier

data class OffsetModifier(
    val offset: IntOffset
) : Modifier.Element<OffsetModifier>, LayoutChangingModifier {
    override fun mergeWith(other: OffsetModifier) = other

    override fun modifyPosition(offset: IntOffset): IntOffset = offset + this.offset
}

@Stable
fun Modifier.offset(x: Int, y: Int) = then(OffsetModifier(IntOffset(x, y)))
