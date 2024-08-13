package ink.pmc.interactive.api.inventory.components

import androidx.compose.runtime.Composable
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.inventory.components.canvases.LocalInventory
import ink.pmc.interactive.api.inventory.modifiers.Modifier
import ink.pmc.interactive.api.inventory.modifiers.fillMaxSize

@Composable
@Suppress("FunctionName")
fun Background(contents: ComposableFunction = ::Placeholder) {
    VerticalGrid(modifier = Modifier.fillMaxSize()) {
        val inv = LocalInventory.current
        repeat(inv.size) {
            contents()
        }
    }
}