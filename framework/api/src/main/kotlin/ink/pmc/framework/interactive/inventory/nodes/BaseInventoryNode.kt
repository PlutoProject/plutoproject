package ink.pmc.framework.interactive.inventory.nodes

import ink.pmc.framework.interactive.canvas.Canvas
import ink.pmc.framework.interactive.inventory.Modifier
import ink.pmc.framework.interactive.inventory.layout.InventoryNode
import ink.pmc.framework.interactive.inventory.layout.MeasurePolicy
import ink.pmc.framework.interactive.inventory.layout.Renderer

interface BaseInventoryNode {

    var measurePolicy: MeasurePolicy
    var renderer: Renderer
    var canvas: Canvas?
    var modifier: Modifier
    var width: Int
    var height: Int
    var x: Int
    var y: Int

    fun render() = renderTo(null)
    fun renderTo(canvas: Canvas?)

    companion object {
        val Constructor: () -> BaseInventoryNode = ::InventoryNode
    }

}

