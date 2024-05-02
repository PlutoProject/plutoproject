package ink.pmc.common.exchange

import org.bukkit.entity.Player

@Suppress("UNUSED")
interface IPaperExchangeService : IExchangeService {

    fun startExchange(player: Player)

    fun endExchange(player: Player)

    suspend fun startExchangeSuspending(player: Player)

    suspend fun endExchangeSuspending(player: Player)

}