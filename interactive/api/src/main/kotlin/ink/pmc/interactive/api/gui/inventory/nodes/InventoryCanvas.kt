package ink.pmc.interactive.api.gui.inventory.nodes

import ink.pmc.interactive.api.gui.inventory.layout.MeasurePolicy
import ink.pmc.interactive.api.gui.inventory.layout.MeasureResult

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
