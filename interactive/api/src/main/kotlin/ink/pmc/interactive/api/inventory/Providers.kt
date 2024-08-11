package ink.pmc.interactive.api.inventory

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import ink.pmc.interactive.api.inventory.canvas.Canvas
import ink.pmc.interactive.api.inventory.canvas.ClickHandler

val LocalClickHandler: ProvidableCompositionLocal<ClickHandler> =
    staticCompositionLocalOf { error("No provider for local click handler") }
val LocalCanvas: ProvidableCompositionLocal<Canvas?> =
    staticCompositionLocalOf { null }