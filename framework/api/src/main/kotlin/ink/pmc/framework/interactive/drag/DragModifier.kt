package ink.pmc.framework.interactive.drag

import ink.pmc.framework.interactive.Modifier

open class DragModifier(
    val merged: Boolean = false,
    val onDrag: (DragScope.() -> Unit),
) : Modifier.Element<DragModifier> {
    override fun mergeWith(other: DragModifier) = DragModifier(merged = true) {
        if (!other.merged)
            onDrag()
        other.onDrag(this)
    }
}

//TODO reimplement properly
//fun Modifier.draggable(onDrag: DragScope.() -> Unit) = then(DragModifier(onDrag = onDrag))
