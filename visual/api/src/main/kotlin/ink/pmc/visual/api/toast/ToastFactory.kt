package ink.pmc.visual.api.toast

import ink.pmc.utils.item.KeyedMaterial
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
object ToastFactory : IToastFactory by IToastFactory.instance

@Suppress("UNUSED")
interface IToastFactory {

    companion object {
        lateinit var instance: IToastFactory
    }

    fun of(
        icon: KeyedMaterial,
        message: Component,
        type: ToastType,
        frame: ToastFrame
    ): Toast

}