package ink.pmc.visual.toast

import net.kyori.adventure.text.Component

@Suppress("UNUSED")
interface Toast {

    val icon: String
    val title: Component
    val description: Component

}