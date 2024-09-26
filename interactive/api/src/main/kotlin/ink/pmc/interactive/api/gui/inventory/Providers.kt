package ink.pmc.interactive.api.gui.inventory

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import ink.pmc.interactive.api.gui.inventory.canvas.Canvas
import ink.pmc.interactive.api.gui.inventory.canvas.ClickHandler

val LocalClickHandler: ProvidableCompositionLocal<ClickHandler> =
    staticCompositionLocalOf { error("No provider for local click handler") }
val LocalCanvas: ProvidableCompositionLocal<Canvas?> =
    staticCompositionLocalOf { null }