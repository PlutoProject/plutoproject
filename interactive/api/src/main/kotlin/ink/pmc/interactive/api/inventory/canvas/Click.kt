package ink.pmc.interactive.api.inventory.canvas

import ink.pmc.interactive.api.inventory.modifiers.click.ClickScope
import ink.pmc.interactive.api.inventory.modifiers.drag.DragScope

data class ClickResult(val cancelBukkitEvent: Boolean? = null) {

    fun mergeWith(other: ClickResult) = ClickResult(
        // Prioritize true > false > null
        cancelBukkitEvent = (cancelBukkitEvent ?: other.cancelBukkitEvent)?.or(other.cancelBukkitEvent ?: false)
    )

}

interface ClickHandler {

    fun processClick(scope: ClickScope): ClickResult
    fun processDrag(scope: DragScope)

}
