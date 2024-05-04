package ink.pmc.common.exchange

import org.bukkit.entity.Player

@Suppress("UNUSED")
interface IPaperExchangeService : IExchangeService {

    val lobby: ExchangeLobby

    suspend fun startExchange(player: Player)

    suspend fun endExchange(player: Player, goBack: Boolean = true)

    suspend fun checkout(player: Player): Long

    fun isInExchange(player: Player): Boolean

}