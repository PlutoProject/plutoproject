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
    override val description: Component,
    override var status: DestinationStatus,
    override var playerCount: Int,
    override val maxPlayerCount: Int,
    override val isHidden: Boolean
) : AbstractDestination() {

    override lateinit var category: Category

    override suspend fun transfer(player: PlayerWrapper<*>) {
        player.playSound {

        }
        player.switchServer(id)
    }

}