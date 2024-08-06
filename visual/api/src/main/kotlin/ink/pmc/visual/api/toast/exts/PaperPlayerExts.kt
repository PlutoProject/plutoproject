package ink.pmc.visual.api.toast.exts

import ink.pmc.visual.api.toast.DefaultBukkitToastRenderer
import ink.pmc.visual.api.toast.Toast
import ink.pmc.visual.api.toast.ToastRenderer
import ink.pmc.visual.api.toast.dsl.ToastDsl
import org.bukkit.entity.Player

fun Player.showToast(toast: Toast, render: ToastRenderer<Player> = DefaultBukkitToastRenderer) {
    render.render(this, toast)
}

fun Player.showToast(render: ToastRenderer<Player> = DefaultBukkitToastRenderer, block: ToastDsl.() -> Unit) {
    val toast = ToastDsl().apply(block).create()
    showToast(toast, render)
}