package ink.pmc.framework.interactive.inventory

import androidx.compose.runtime.Composable
import ink.pmc.framework.interactive.ComposableFunction
import ink.pmc.framework.interactive.inventory.components.canvases.LocalInventory

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