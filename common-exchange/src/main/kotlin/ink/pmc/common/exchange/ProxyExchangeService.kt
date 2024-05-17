package ink.pmc.common.exchange

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.exchange.proto.ExchangeRpc
import ink.pmc.common.rpc.RpcServer

class ProxyExchangeService : AbstractProxyExchangeService() {

    override val rpc: ExchangeRpc = ExchangeRpc(this)

    init {
        RpcServer.apply { addService(rpc) }
    }

    override suspend fun startExchange(player: Player) {
        TODO("Not yet implemented")
    }

    override suspend fun endExchange(player: Player) {
        TODO("Not yet implemented")
    }

    override suspend fun isInExchange(player: Player): Boolean {
        return inExchange.contains(player)
    }

}