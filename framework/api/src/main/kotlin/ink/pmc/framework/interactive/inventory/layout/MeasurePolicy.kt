package ink.pmc.framework.interactive.inventory.layout

import androidx.compose.runtime.Stable
import ink.pmc.framework.interactive.canvas.Canvas
import ink.pmc.framework.interactive.inventory.Constraints
import ink.pmc.framework.interactive.inventory.nodes.BaseInventoryNode
import ink.pmc.framework.interactive.inventory.state.IntOffset
import ink.pmc.framework.interactive.inventory.state.IntSize

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
    fun Canvas.render(node: BaseInventoryNode) {}
    fun Canvas.renderAfterChildren(node: BaseInventoryNode) {}
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


