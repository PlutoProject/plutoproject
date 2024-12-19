package ink.pmc.framework.interactive.nodes

import ink.pmc.framework.interactive.canvas.Canvas
import ink.pmc.framework.interactive.Modifier
import ink.pmc.framework.interactive.layout.InventoryNode
import ink.pmc.framework.interactive.layout.MeasurePolicy
import ink.pmc.framework.interactive.layout.Renderer

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

