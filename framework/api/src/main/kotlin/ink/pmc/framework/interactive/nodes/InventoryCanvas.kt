package ink.pmc.framework.interactive.nodes

import ink.pmc.framework.interactive.layout.MeasurePolicy
import ink.pmc.framework.interactive.layout.MeasureResult

interface InventoryCloseScope {
    fun reopen()
}

val StaticMeasurePolicy = MeasurePolicy { measurables, constraints ->
    val noMinConstraints = constraints.copy(minWidth = 0, minHeight = 0)
    val placeables = measurables.map { it.measure(noMinConstraints) }
    MeasureResult(constraints.minWidth, constraints.minHeight) {
        placeables.forEach { it.placeAt(0, 0) }
    }
}
