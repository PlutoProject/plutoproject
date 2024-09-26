package ink.pmc.interactive.api.gui.inventory.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ink.pmc.interactive.api.gui.inventory.jetpack.Alignment
import ink.pmc.interactive.api.gui.inventory.jetpack.Arrangement
import ink.pmc.interactive.api.gui.inventory.jetpack.LayoutDirection
import ink.pmc.interactive.api.gui.inventory.modifiers.Modifier

/**
 * A layout component that places contents in a row left-to-right.
 */
@Composable
fun Row(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable () -> Unit
) {
    val measurePolicy = remember(horizontalArrangement, verticalAlignment) {
        RowMeasurePolicy(
            horizontalArrangement,
            verticalAlignment
        )
    }
    Layout(
        measurePolicy,
        modifier = modifier,
        content = content
    )
}

private data class RowMeasurePolicy(
    private val horizontalArrangement: Arrangement.Horizontal,
    private val verticalAlignment: Alignment.Vertical,
) : RowColumnMeasurePolicy(
    sumWidth = true,
    arrangementSpacing = horizontalArrangement.spacing
) {
    override fun placeChildren(placeables: List<Placeable>, width: Int, height: Int): MeasureResult {
        val positions = IntArray(placeables.size)
        horizontalArrangement.arrange(
            totalSize = width,
            sizes = placeables.map { it.width }.toIntArray(),
            layoutDirection = LayoutDirection.Ltr,
            outPositions = positions
        )
        return MeasureResult(width, height) {
            placeables.forEachIndexed { index, child ->
                child.placeAt(positions[index], verticalAlignment.align(child.height, height))
            }
        }
    }
}
