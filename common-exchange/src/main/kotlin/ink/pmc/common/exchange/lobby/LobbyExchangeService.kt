package ink.pmc.common.exchange.lobby

import ink.pmc.common.exchange.backend.AbstractBackendExchangeService
import ink.pmc.common.exchange.proto.lobby2proxy.lobbyHealthReport
import ink.pmc.common.utils.concurrent.submitAsyncIO
import ink.pmc.common.utils.platform.paper
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.entity.Player
import java.time.Instant

lateinit var lobbyExchangeService: LobbyExchangeService

class LobbyExchangeService(private val lobby: World) : AbstractBackendExchangeService() {

    private lateinit var lobbyHealthReporter: Job

    init {
        lobbyExchangeService = this
        initWorldEnvironment()
        startBackGroundJobs()
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

    override fun startBackGroundJobs() {
        lobbyHealthReporter = reportLobbyHealth()
    }

    private fun reportLobbyHealth(): Job {
        return submitAsyncIO {
            stub.reportLobbyHealth(lobbyHealthReport {
                serviceId = id.toString()
                time = Instant.now().toEpochMilli()
                playerCount = paper.onlinePlayers.size
            })
            delay(5000)
        }
    }

    override suspend fun stopBackGroundJobs() {
        lobbyHealthReporter.cancelAndJoin()
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