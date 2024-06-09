package ink.pmc.visual.api.toast.exts

import ink.pmc.visual.api.Renderer
import ink.pmc.visual.api.toast.Toast
import ink.pmc.visual.api.toast.ToastRenderer
import ink.pmc.visual.api.toast.dsl.ToastDsl
import org.bukkit.entity.Player

@Suppress("UNCHECKED_CAST")
private fun paperRender(): Renderer<Player, Toast> {
    return ToastRenderer.defaultRenderer as ToastRenderer<Player>
}

fun Player.showToast(toast: Toast) {
    paperRender().render(this, toast)
}

fun Player.showToast(block: ToastDsl.() -> Unit) {
    val toast = ToastDsl().apply(block).create()
    showToast(toast)
}