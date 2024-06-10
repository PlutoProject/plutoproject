package ink.pmc.visual.api.toast.exts

import ink.pmc.visual.api.toast.Toast
import ink.pmc.visual.api.toast.ToastRenderer
import ink.pmc.visual.api.toast.dsl.ToastDsl
import org.bukkit.entity.Player

@Suppress("UNCHECKED_CAST")
private fun paperRender(): ToastRenderer<Player> {
    return ToastRenderer.defaultRenderer as ToastRenderer<Player>
}

fun Player.showToast(toast: Toast, render: ToastRenderer<Player> = paperRender()) {
    render.render(this, toast)
}

fun Player.showToast(render: ToastRenderer<Player> = paperRender(), block: ToastDsl.() -> Unit) {
    val toast = ToastDsl().apply(block).create()
    showToast(toast, render)
}