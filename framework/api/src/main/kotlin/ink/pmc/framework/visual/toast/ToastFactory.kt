package ink.pmc.framework.visual.toast

import ink.pmc.framework.inject.inlinedGet
import ink.pmc.framework.item.KeyedMaterial
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
interface ToastFactory {
    companion object : ToastFactory by inlinedGet()

    fun of(
        icon: KeyedMaterial,
        message: Component,
        type: ToastType,
        frame: ToastFrame
    ): Toast
}