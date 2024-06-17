package ink.pmc.transfer

import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.proto.TransferRpc
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import ink.pmc.utils.platform.proxy

class ProxyTransferService : AbstractProxyTransferService() {

    override val protocol: TransferRpc = TransferRpc(proxy, this)
    override val conditionManager: ConditionManager = ConditionManagerImpl(this)
    override var playerCount: Int = 0

    override suspend fun transferPlayer(player: PlayerWrapper<*>, id: String) {
        val destination = getDestination(id) ?: throw IllegalStateException("Destination named as $id not existed")
        destination.transfer(player)
    }

    override fun close() {
        protocol.close()
    }

}