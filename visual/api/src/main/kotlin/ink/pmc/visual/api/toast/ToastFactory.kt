package ink.pmc.visual.api.toast

import ink.pmc.utils.multiplaform.item.KeyedMaterial
import net.kyori.adventure.text.Component
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED")
interface ToastFactory {

    companion object : ToastFactory by object : KoinComponent {
        val instance by inject<ToastFactory>()
    }.instance

    fun of(
        icon: KeyedMaterial,
        message: Component,
        type: ToastType,
        frame: ToastFrame
    ): Toast

}