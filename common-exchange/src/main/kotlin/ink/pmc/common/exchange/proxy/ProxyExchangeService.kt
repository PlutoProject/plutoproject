package ink.pmc.common.exchange.proxy

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.exchange.lobby.ExchangeSession
import ink.pmc.common.exchange.lobbyServerName
import ink.pmc.common.exchange.proto.ExchangeRpc
import ink.pmc.common.rpc.RpcServer
import ink.pmc.common.utils.player.switchServer

class ProxyExchangeService : AbstractProxyExchangeService() {

    override val rpc: ExchangeRpc = ExchangeRpc(this)

    init {
        RpcServer.apply { addService(rpc) }
    }

    override suspend fun startExchange(player: Player) {
        if (isInExchange(player)) {
            return
        }

        val original = if (player.currentServer.isPresent) player.currentServer.get().serverInfo.name else return
        player.switchServer(lobbyServerName)
        inExchange[player] = ExchangeSession(original)
    }

    override suspend fun endExchange(player: Player) {
        if (!isInExchange(player)) {
            return
        }

        val originalServer = inExchange[player]!!.originalServer
        player.switchServer(originalServer)
        inExchange.remove(player)
    }

    override suspend fun isInExchange(player: Player): Boolean {
        return inExchange.contains(player)
    }

}