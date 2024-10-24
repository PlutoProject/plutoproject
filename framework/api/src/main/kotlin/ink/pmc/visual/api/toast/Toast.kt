package ink.pmc.visual.api.toast

import ink.pmc.framework.utils.item.KeyedMaterial
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
interface Toast {

    val icon: KeyedMaterial
    val message: Component
    val type: ToastType
    val frame: ToastFrame

}