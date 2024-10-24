package ink.pmc.transfer

import ink.pmc.transfer.api.Category
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.framework.utils.multiplaform.item.KeyedMaterial
import ink.pmc.framework.utils.multiplaform.player.PlayerWrapper
import net.kyori.adventure.text.Component

class DestinationImpl(
    private val service: AbstractTransferService,
    override val id: String,
    override val icon: KeyedMaterial,
    override val name: Component,
    override val description: List<Component>,
    override val category: Category?,
    override var status: DestinationStatus,
    override var playerCount: Int,
    override var maxPlayerCount: Int,
    override val isHidden: Boolean
) : AbstractDestination() {

    override suspend fun transfer(player: PlayerWrapper<*>) {
        service.transferPlayer(player, id)
    }

}