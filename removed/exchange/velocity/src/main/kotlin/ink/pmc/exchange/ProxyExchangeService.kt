package ink.pmc.exchange

import com.velocitypowered.api.proxy.Player
import ink.pmc.exchange.proto.ExchangeRpc
import ink.pmc.rpc.api.RpcServer
import ink.pmc.utils.player.switchServer
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant

class ProxyExchangeService : AbstractProxyExchangeService() {

    override val rpc: ExchangeRpc = ExchangeRpc(this)
    override val lastHealthReportTime: MutableStateFlow<Instant?> = MutableStateFlow(null)

    init {
        RpcServer.apply { addService(rpc) }
    }

    override suspend fun startExchange(player: Player) {
        if (isInExchange(player)) {
            return
        }

        if (!isLobbyHealthy()) {
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

        if (!isLobbyHealthy()) {
            return
        }

        val originalServer = inExchange[player]!!.originalServer
        player.switchServer(originalServer)
        inExchange.remove(player)
    }

    override suspend fun isInExchange(player: Player): Boolean {
        return inExchange.contains(player)
    }

    override fun isLobbyHealthy(): Boolean {
        if (lastHealthReportTime.value == null) {
            return false
        }

        return lastHealthReportTime.value!!.plusSeconds(8).isBefore(Instant.now())
    }

}