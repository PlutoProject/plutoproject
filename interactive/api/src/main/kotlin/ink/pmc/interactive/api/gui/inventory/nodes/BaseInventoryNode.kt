package ink.pmc.interactive.api.gui.inventory.nodes

import ink.pmc.interactive.api.gui.inventory.canvas.Canvas
import ink.pmc.interactive.api.gui.inventory.layout.InventoryNode
import ink.pmc.interactive.api.gui.inventory.layout.MeasurePolicy
import ink.pmc.interactive.api.gui.inventory.layout.Renderer
import ink.pmc.interactive.api.gui.inventory.modifiers.Modifier

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

