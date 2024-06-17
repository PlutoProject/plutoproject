package ink.pmc.transfer

import ink.pmc.transfer.api.Category
import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.proto.TransferRpc
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import ink.pmc.utils.platform.proxy

class ProxyTransferService : AbstractProxyTransferService() {

    override val protocol: TransferRpc = TransferRpc(proxy, this)
    override val conditionManager: ConditionManager = ConditionManagerImpl(this)
    override var playerCount: Int = 0

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

    override suspend fun transferPlayer(player: PlayerWrapper<*>, id: String) {
        TODO("Not yet implemented")
    }

}