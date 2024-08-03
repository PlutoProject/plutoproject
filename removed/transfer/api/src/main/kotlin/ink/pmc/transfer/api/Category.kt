package ink.pmc.transfer.api

import ink.pmc.utils.multiplaform.item.KeyedMaterial
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
interface Category {

    val id: String
    val icon: KeyedMaterial
    val name: Component
    val description: List<Component>
    val playerCount: Int
    val destinations: Set<Destination>

}