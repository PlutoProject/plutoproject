package ink.pmc.visual.toast.exts

import ink.pmc.visual.Renderer
import ink.pmc.visual.toast.Toast
import ink.pmc.visual.toast.ToastRenderer
import ink.pmc.visual.toast.dsl.ToastDsl
import org.bukkit.entity.Player

@Suppress("UNCHECKED_CAST")
private fun paperRender(): Renderer<Player, Toast> {
    return ToastRenderer.defaultRenderer as Renderer<Player, Toast>
}

fun Player.showToast(toast: Toast) {
    paperRender().render(this, toast)
}

fun Player.showToast(block: ToastDsl.() -> Unit) {
    val toast = ToastDsl().apply(block).create()
    showToast(toast)
}