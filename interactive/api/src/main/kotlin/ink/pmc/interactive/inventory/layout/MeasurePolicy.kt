package ink.pmc.interactive.inventory.layout

import androidx.compose.runtime.Stable
import ink.pmc.interactive.inventory.components.state.IntOffset
import ink.pmc.interactive.inventory.components.state.IntSize
import ink.pmc.interactive.inventory.inventory.GuiyCanvas
import ink.pmc.interactive.inventory.modifiers.Constraints
import ink.pmc.interactive.inventory.nodes.GuiyNode

data class MeasureResult(
    val width: Int,
    val height: Int,
    val placer: Placer,
)

@Stable
fun interface MeasurePolicy {
    fun measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult
}

@Stable
fun interface Placer {
    fun placeChildren()
}

@Stable
interface Renderer {
    fun GuiyCanvas.render(node: GuiyNode) {}
    fun GuiyCanvas.renderAfterChildren(node: GuiyNode) {}
}

interface Measurable {
    fun measure(constraints: Constraints): Placeable
}

interface Placeable {
    var width: Int
    var height: Int

    fun placeAt(x: Int, y: Int)

    fun placeAt(offset: IntOffset) = placeAt(offset.x, offset.y)

    val size: IntSize get() = IntSize(width, height)
}


