package ink.pmc.framework.visual.toast.exts

import ink.pmc.framework.visual.toast.BukkitDefaultToastRenderer
import ink.pmc.framework.visual.toast.ToastRenderer
import ink.pmc.framework.visual.toast.dsl.ToastDsl
import org.bukkit.entity.Player

fun Player.showToast(
    toast: ink.pmc.framework.visual.toast.Toast,
    render: ToastRenderer<Player> = BukkitDefaultToastRenderer
) {
    render.render(this, toast)
}

fun Player.showToast(
    render: ToastRenderer<Player> = BukkitDefaultToastRenderer,
    block: ToastDsl.() -> Unit
) {
    val toast = ToastDsl().apply(block).create()
    showToast(toast, render)
}