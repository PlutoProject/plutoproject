package ink.pmc.interactive.inventory.nodes

import ink.pmc.interactive.inventory.canvas.Canvas
import ink.pmc.interactive.inventory.layout.LayoutNode
import ink.pmc.interactive.inventory.layout.MeasurePolicy
import ink.pmc.interactive.inventory.layout.Renderer
import ink.pmc.interactive.inventory.modifiers.Modifier

interface InvNode {

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
        val Constructor: () -> InvNode = ::LayoutNode
    }

}

