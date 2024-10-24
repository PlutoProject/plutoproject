package ink.pmc.framework.visual

import ink.pmc.framework.utils.item.KeyedMaterial
import ink.pmc.visual.api.toast.Toast
import ink.pmc.visual.api.toast.ToastFactory
import ink.pmc.visual.api.toast.ToastFrame
import ink.pmc.visual.api.toast.ToastType
import net.kyori.adventure.text.Component

class ToastFactoryImpl : ToastFactory {
    override fun of(icon: KeyedMaterial, message: Component, type: ToastType, frame: ToastFrame): Toast {
        return ToastImpl(icon, message, type, frame)
    }
}