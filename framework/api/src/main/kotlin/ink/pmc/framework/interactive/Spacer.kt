package ink.pmc.framework.interactive

import androidx.compose.runtime.Composable
import ink.pmc.framework.interactive.layout.Layout
import ink.pmc.framework.interactive.layout.MeasureResult

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

@Composable
@Suppress("FunctionName")
fun ItemSpacer() {
    Spacer(modifier = Modifier.width(1).height(1))
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
    Spacer(
        modifier
            .run { if (width != null) width(width) else this }
            .run { if (height != null) height(height) else this }
    )
}