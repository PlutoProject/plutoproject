package ink.pmc.transfer

import ink.pmc.transfer.api.Category
import ink.pmc.transfer.api.Destination
import ink.pmc.utils.multiplaform.player.PlayerWrapper

abstract class BaseTransferServiceImpl : AbstractTransferService() {

    override val playerCount: Int
        get() = destinations.sumOf { it.playerCount }

    override suspend fun transferPlayer(player: PlayerWrapper<*>, id: String) {
        val destination = getDestination(id) ?: throw IllegalStateException("Destination named as $id not existed")
        destination.transfer(player)
    }

    override fun getDestination(id: String): Destination? {
        return destinations.firstOrNull { it.id == id }
    }

    override fun hasDestination(id: String): Boolean {
        return destinations.any { it.id == id }
    }

    override fun getCategory(id: String): Category? {
        return categories.firstOrNull { it.id == id }
    }

    override fun hasCategory(id: String): Boolean {
        return categories.any { it.id == id }
    }

}