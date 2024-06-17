package ink.pmc.transfer

import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.rpc.api.IRpcServer
import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.proto.TransferRpc

class ProxyTransferService(proxyServer: ProxyServer, rpc: IRpcServer) : AbstractProxyTransferService() {

    override val protocol: TransferRpc = TransferRpc(proxyServer, this)
    override val conditionManager: ConditionManager = ConditionManagerImpl(this)

    init {
        rpc.apply { addService(protocol) }
    }

    override fun close() {
        protocol.close()
    }

}