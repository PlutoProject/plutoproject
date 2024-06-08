package ink.pmc.exchange.proxy

import com.velocitypowered.api.proxy.Player
import ink.pmc.exchange.proto.ExchangeRpc
import ink.pmc.exchange.service.BaseExchangeServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant

abstract class AbstractProxyExchangeService : BaseExchangeServiceImpl<Player>() {

    abstract val rpc: ExchangeRpc
    abstract val lastHealthReportTime: MutableStateFlow<Instant?>
    val inExchange: MutableMap<Player, ExchangeSession> = mutableMapOf()

    abstract fun isLobbyHealthy(): Boolean

}