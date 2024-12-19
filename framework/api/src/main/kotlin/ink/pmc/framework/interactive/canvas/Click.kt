package ink.pmc.framework.interactive.canvas

import ink.pmc.framework.interactive.click.ClickScope
import ink.pmc.framework.interactive.drag.DragScope

data class ClickResult(val cancelBukkitEvent: Boolean? = null) {

    fun mergeWith(other: ClickResult) = ClickResult(
        // Prioritize true > false > null
        cancelBukkitEvent = (cancelBukkitEvent ?: other.cancelBukkitEvent)?.or(other.cancelBukkitEvent ?: false)
    )

}

interface ClickHandler {

    suspend fun processClick(scope: ClickScope): ClickResult
    suspend fun processDrag(scope: DragScope)

}
