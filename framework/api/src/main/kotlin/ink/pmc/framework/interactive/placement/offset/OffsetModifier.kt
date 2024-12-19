package ink.pmc.framework.interactive.placement.offset

import androidx.compose.runtime.Stable
import ink.pmc.framework.interactive.LayoutChangingModifier
import ink.pmc.framework.interactive.Modifier
import ink.pmc.framework.interactive.state.IntOffset

data class OffsetModifier(
    val offset: IntOffset
) : Modifier.Element<OffsetModifier>, LayoutChangingModifier {
    override fun mergeWith(other: OffsetModifier) = other

    override fun modifyPosition(offset: IntOffset): IntOffset = offset + this.offset
}

@Stable
fun Modifier.offset(x: Int, y: Int) = then(OffsetModifier(IntOffset(x, y)))
