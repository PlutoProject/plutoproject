package ink.pmc.transfer

import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.proto.TransferRpc
import ink.pmc.utils.platform.proxy

class ProxyTransferService : AbstractProxyTransferService() {

    override val protocol: TransferRpc = TransferRpc(proxy, this)
    override val conditionManager: ConditionManager = ConditionManagerImpl(this)

    override fun close() {
        protocol.close()
    }

}