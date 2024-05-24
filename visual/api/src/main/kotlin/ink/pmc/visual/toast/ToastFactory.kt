package ink.pmc.visual.toast

import net.kyori.adventure.text.Component

@Suppress("UNUSED")
object ToastFactory : IToastFactory by IToastFactory.instance

@Suppress("UNUSED")
interface IToastFactory {

    companion object {
        lateinit var instance: IToastFactory
    }

    fun of(icon: String, title: Component, description: Component): Toast

}