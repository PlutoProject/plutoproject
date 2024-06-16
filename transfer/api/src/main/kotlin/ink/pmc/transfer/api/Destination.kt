package ink.pmc.transfer.api

import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
interface Destination {

    val id: String
    val icon: KeyedMaterial
    val name: Component
    val description: Component
    val categories: Set<Category>
    val status: DestinationStatus
    val playerCount: Int
    val maxPlayerCount: Int
    val isHidden: Boolean
    val condition: (player: PlayerWrapper<*>) -> Boolean

    suspend fun transfer(player: PlayerWrapper<*>)

}