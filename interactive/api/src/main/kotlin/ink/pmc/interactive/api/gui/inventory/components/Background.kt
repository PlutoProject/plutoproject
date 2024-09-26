package ink.pmc.interactive.api.gui.inventory.components

import androidx.compose.runtime.Composable
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.gui.inventory.components.canvases.LocalInventory
import ink.pmc.interactive.api.gui.inventory.modifiers.Modifier
import ink.pmc.interactive.api.gui.inventory.modifiers.fillMaxSize

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