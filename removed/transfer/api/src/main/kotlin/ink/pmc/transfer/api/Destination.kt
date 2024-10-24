package ink.pmc.transfer.api

import ink.pmc.framework.utils.multiplaform.item.KeyedMaterial
import ink.pmc.framework.utils.multiplaform.player.PlayerWrapper
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
interface Destination {

    val id: String
    val icon: KeyedMaterial
    val name: Component
    val description: List<Component>
    val category: Category?
    val status: DestinationStatus
    val playerCount: Int
    val maxPlayerCount: Int
    val isHidden: Boolean

    suspend fun transfer(player: PlayerWrapper<*>)

}