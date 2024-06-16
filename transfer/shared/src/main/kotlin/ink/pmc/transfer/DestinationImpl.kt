package ink.pmc.transfer

import ink.pmc.transfer.api.Category
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import net.kyori.adventure.text.Component

class DestinationImpl(
    override val id: String,
    override val icon: KeyedMaterial,
    override val name: Component,
    override val categories: Set<Category>,
    override val description: Component,
    override var status: DestinationStatus,
    override val playerCount: Int,
    override val maxPlayerCount: Int,
    override val isHidden: Boolean,
    override val condition: (player: PlayerWrapper<*>) -> Boolean
) : AbstractDestination() {

    override suspend fun transfer(player: PlayerWrapper<*>) {
        player.switchServer(id)
    }

}