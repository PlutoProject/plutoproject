package ink.pmc.transfer

import ink.pmc.transfer.api.Category
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import net.kyori.adventure.text.Component

class DestinationImpl : Destination {

    override val id: String
        get() = TODO("Not yet implemented")
    override val icon: KeyedMaterial
        get() = TODO("Not yet implemented")
    override val name: Component
        get() = TODO("Not yet implemented")
    override val categories: Set<Category>
        get() = TODO("Not yet implemented")
    override val description: Component
        get() = TODO("Not yet implemented")
    override val status: DestinationStatus
        get() = TODO("Not yet implemented")
    override val playerCount: Int
        get() = TODO("Not yet implemented")
    override val maxPlayerCount: Int
        get() = TODO("Not yet implemented")
    override val hidden: Boolean
        get() = TODO("Not yet implemented")
    override val condition: (player: PlayerWrapper<*>) -> Boolean
        get() = TODO("Not yet implemented")

    override suspend fun transfer(player: PlayerWrapper<*>) {
        player.switchServer(id)
    }

}