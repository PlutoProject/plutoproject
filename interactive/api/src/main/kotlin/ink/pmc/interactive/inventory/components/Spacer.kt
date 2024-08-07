package ink.pmc.interactive.inventory.components

import androidx.compose.runtime.Composable
import ink.pmc.interactive.inventory.layout.Layout
import ink.pmc.interactive.inventory.layout.MeasureResult
import ink.pmc.interactive.inventory.modifiers.Modifier
import ink.pmc.interactive.inventory.modifiers.height
import ink.pmc.interactive.inventory.modifiers.width

/**
 * A layout element that takes up space without drawing anything.
 */
@Composable
@Suppress("FunctionName")
fun Spacer(modifier: Modifier = Modifier) {
    Layout(
        measurePolicy = { _, constraints ->
            MeasureResult(constraints.minWidth, constraints.minHeight) {}
        },
        modifier = modifier,
    )
}

/**
 * A layout element that takes up space without drawing anything.
 *
 * @param width The width of the spacer.
 * @param height The height of the spacer.
 */
@Composable
@Suppress("FunctionName")
fun Spacer(width: Int? = null, height: Int? = null, modifier: Modifier = Modifier) {
    Spacer(modifier
        .run { if (width != null) width(width) else this }
        .run { if (height != null) height(height) else this }
    )
}
