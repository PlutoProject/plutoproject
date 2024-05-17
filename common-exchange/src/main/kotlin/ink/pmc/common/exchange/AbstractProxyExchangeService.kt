package ink.pmc.common.exchange

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.exchange.proto.ExchangeRpc

abstract class AbstractProxyExchangeService : BaseExchangeServiceImpl<Player>() {

    abstract val rpc: ExchangeRpc
    val inExchange: List<Player> = mutableListOf()

}