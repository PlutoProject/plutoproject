package ink.pmc.transfer

import net.kyori.adventure.text.Component

@Suppress("UNUSED")
interface Category {

    val id: String
    val name: Component
    val description: Component
    val totalPlayers: Int

}