package ink.pmc.visual.toast.dsl

import ink.pmc.visual.toast.Toast
import ink.pmc.visual.toast.ToastFactory
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
class ToastDsl {

    lateinit var icon: String
    lateinit var title: Component
    lateinit var description: Component

    fun create(): Toast {
        return ToastFactory.of(icon, title, description)
    }

}