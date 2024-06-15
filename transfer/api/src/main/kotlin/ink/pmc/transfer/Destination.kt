package ink.pmc.transfer

import ink.pmc.utils.multiplaform.item.KeyedMaterial
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
interface Destination {

    val id: String
    val icon: KeyedMaterial
    val name: Component
    val description: Component
    val inMaintenance: Boolean
    val players: Int
    val hidden: Boolean

}