package ink.pmc.framework.interactive.inventory.placement.offset

import androidx.compose.runtime.Stable
import ink.pmc.interactive.api.inventory.components.state.IntOffset
import ink.pmc.framework.interactive.inventory.LayoutChangingModifier
import ink.pmc.framework.interactive.inventory.Modifier

data class OffsetModifier(
    val offset: IntOffset
) : Modifier.Element<OffsetModifier>, LayoutChangingModifier {
    override fun mergeWith(other: OffsetModifier) = other

    override fun modifyPosition(offset: IntOffset): IntOffset = offset + this.offset
}

@Stable
fun Modifier.offset(x: Int, y: Int) = then(OffsetModifier(IntOffset(x, y)))