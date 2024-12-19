package ink.pmc.framework.visual.toast

import ink.pmc.framework.item.KeyedMaterial
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
interface Toast {

    val icon: KeyedMaterial
    val message: Component
    val type: ToastType
    val frame: ToastFrame

}