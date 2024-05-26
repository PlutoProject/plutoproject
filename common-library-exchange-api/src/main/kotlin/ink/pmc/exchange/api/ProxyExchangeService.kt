package ink.pmc.exchange.api

import com.velocitypowered.api.proxy.Player

@Suppress("UNCHECKED_CAST")
object ProxyExchangeService : IExchangeService<Player> by IExchangeService.instance as IExchangeService<Player>