package ink.pmc.exchange.api

import org.bukkit.entity.Player

@Suppress("UNCHECKED_CAST")
object BackendExchangeService : IExchangeService<Player> by IExchangeService.instance as IExchangeService<Player>