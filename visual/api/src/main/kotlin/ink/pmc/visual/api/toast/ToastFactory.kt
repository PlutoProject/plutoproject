package ink.pmc.visual.api.toast

import ink.pmc.utils.inject.inlinedGet
import ink.pmc.utils.item.KeyedMaterial
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