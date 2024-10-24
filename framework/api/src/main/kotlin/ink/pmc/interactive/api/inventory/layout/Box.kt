package ink.pmc.interactive.api.inventory.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ink.pmc.interactive.api.inventory.components.state.IntSize
import ink.pmc.framework.interactive.inventory.jetpack.Alignment
import ink.pmc.framework.interactive.inventory.jetpack.LayoutDirection
import ink.pmc.interactive.api.inventory.modifiers.Modifier

@Composable
@Suppress("FunctionName")
fun Box(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit
) {
    val measurePolicy = remember(contentAlignment) { BoxMeasurePolicy(contentAlignment) }
    Layout(
        measurePolicy,
        modifier = modifier,
        content = content
    )
}

internal data class BoxMeasurePolicy(
    private val alignment: Alignment,
) : RowColumnMeasurePolicy() {
    override fun placeChildren(placeables: List<Placeable>, width: Int, height: Int): MeasureResult {
        return MeasureResult(width, height) {
            for (child in placeables) {
                child.placeAt(alignment.align(child.size, IntSize(width, height), LayoutDirection.Ltr))
            }
        }
    }
}
