package ink.pmc.transfer

import ink.pmc.transfer.api.Category
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.api.ITransferService
import ink.pmc.utils.multiplaform.player.PlayerWrapper

class BaseTransferServiceImpl : ITransferService {

    override val playerCount: Int
        get() = TODO("Not yet implemented")

    override fun getDestination(id: String): Destination? {
        TODO("Not yet implemented")
    }

    override fun hasDestination(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCategory(id: String): Category? {
        TODO("Not yet implemented")
    }

    override fun hasCategory(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun transferPlayer(player: PlayerWrapper<*>, id: String) {
        TODO("Not yet implemented")
    }

}