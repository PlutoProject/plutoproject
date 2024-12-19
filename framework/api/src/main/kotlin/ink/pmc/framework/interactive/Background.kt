package ink.pmc.framework.interactive

import androidx.compose.runtime.Composable
import ink.pmc.framework.interactive.canvas.LocalInventory

@Composable
@Suppress("FunctionName")
fun Background(contents: ComposableFunction = { Placeholder() }) {
    VerticalGrid(modifier = Modifier.fillMaxSize()) {
        val inv = LocalInventory.current
        repeat(inv.size) {
            contents()
        }
    }
}