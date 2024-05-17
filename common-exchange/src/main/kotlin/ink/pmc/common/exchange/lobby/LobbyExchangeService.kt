package ink.pmc.common.exchange.lobby

import ink.pmc.common.exchange.paper.AbstractBackendExchangeService
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.entity.Player

class LobbyExchangeService(private val lobby: World) : AbstractBackendExchangeService() {

    init {
        initWorldEnvironment()
    }

    private fun initWorldEnvironment() {
        lobby.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        lobby.setGameRule(GameRule.DO_MOB_LOOT, false)
        lobby.setGameRule(GameRule.MOB_GRIEFING, false)
        lobby.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        lobby.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        lobby.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        lobby.time = 1000
    }

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