package ink.pmc.transfer

import ink.pmc.utils.multiplaform.item.KeyedMaterial
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
interface Category {

    val id: String
    val icon: KeyedMaterial
    val name: Component
    val description: Component
    val totalPlayers: Int

}