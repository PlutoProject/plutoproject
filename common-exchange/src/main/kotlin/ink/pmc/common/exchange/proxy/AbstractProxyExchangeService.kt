package ink.pmc.common.exchange.proxy

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.exchange.lobby.ExchangeSession
import ink.pmc.common.exchange.proto.ExchangeRpc
import ink.pmc.common.exchange.service.BaseExchangeServiceImpl

abstract class AbstractProxyExchangeService : BaseExchangeServiceImpl<Player>() {

    abstract val rpc: ExchangeRpc
    val inExchange: MutableMap<Player, ExchangeSession> = mutableMapOf()

}