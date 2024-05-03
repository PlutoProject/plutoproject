package ink.pmc.common.exchange

import org.bukkit.entity.Player

@Suppress("UNUSED")
interface IPaperExchangeService : IExchangeService {

    val lobby: ExchangeLobby

    fun startExchange(player: Player)

    fun endExchange(player: Player, goBack: Boolean = true)

    fun isInExchange(player: Player): Boolean

}