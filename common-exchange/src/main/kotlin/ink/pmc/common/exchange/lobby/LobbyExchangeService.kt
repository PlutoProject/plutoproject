package ink.pmc.common.exchange.lobby

import ink.pmc.common.exchange.paper.AbstractBackendExchangeService
import org.bukkit.entity.Player

class LobbyExchangeService : AbstractBackendExchangeService() {

    override suspend fun startExchange(player: Player) {
        throw UnsupportedOperationException("Operation only available for normal servers")
    }

    override suspend fun endExchange(player: Player) {
        throw UnsupportedOperationException("Operation only available for normal servers")
    }

    override suspend fun isInExchange(player: Player): Boolean {
        return player.isOnline
    }

}