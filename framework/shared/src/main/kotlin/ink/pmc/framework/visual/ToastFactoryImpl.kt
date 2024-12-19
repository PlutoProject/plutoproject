package ink.pmc.framework.visual

import ink.pmc.framework.item.KeyedMaterial
import ink.pmc.framework.visual.toast.ToastFactory
import ink.pmc.framework.visual.toast.ToastFrame
import ink.pmc.framework.visual.toast.ToastType
import net.kyori.adventure.text.Component

class ToastFactoryImpl : ToastFactory {
    override fun of(
        icon: KeyedMaterial,
        message: Component,
        type: ToastType,
        frame: ToastFrame
    ): ink.pmc.framework.visual.toast.Toast {
        return ToastImpl(icon, message, type, frame)
    }
}