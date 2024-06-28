package ink.pmc.visual

import ink.pmc.utils.item.KeyedMaterial
import ink.pmc.visual.api.toast.IToastFactory
import ink.pmc.visual.api.toast.Toast
import ink.pmc.visual.api.toast.ToastFrame
import ink.pmc.visual.api.toast.ToastType
import net.kyori.adventure.text.Component

object ToastFactoryImpl : IToastFactory {

    override fun of(icon: KeyedMaterial, message: Component, type: ToastType, frame: ToastFrame): Toast {
        return ToastImpl(icon, message, type, frame)
    }
}