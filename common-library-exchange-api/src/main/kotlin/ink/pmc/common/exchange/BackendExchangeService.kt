package ink.pmc.common.exchange

import org.bukkit.entity.Player

@Suppress("UNCHECKED_CAST")
object BackendExchangeService : IExchangeService<Player> by IExchangeService.instance as IExchangeService<Player>