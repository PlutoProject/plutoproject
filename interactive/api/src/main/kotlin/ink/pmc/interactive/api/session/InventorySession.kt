package ink.pmc.interactive.api.session

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import ink.pmc.interactive.api.inventory.canvas.Canvas
import ink.pmc.interactive.api.inventory.canvas.ClickHandler
import ink.pmc.interactive.api.inventory.layout.LayoutNode

val LocalClickHandler: ProvidableCompositionLocal<ClickHandler> =
    staticCompositionLocalOf { error("No provider for local click handler") }
val LocalCanvas: ProvidableCompositionLocal<Canvas?> =
    staticCompositionLocalOf { null }

interface InventorySession : Session<LayoutNode>