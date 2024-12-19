package ink.pmc.framework.interactive

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import ink.pmc.framework.interactive.canvas.Canvas
import ink.pmc.framework.interactive.canvas.ClickHandler

val LocalClickHandler: ProvidableCompositionLocal<ClickHandler> =
    staticCompositionLocalOf { error("No provider for local click handler") }
val LocalCanvas: ProvidableCompositionLocal<Canvas?> =
    staticCompositionLocalOf { null }