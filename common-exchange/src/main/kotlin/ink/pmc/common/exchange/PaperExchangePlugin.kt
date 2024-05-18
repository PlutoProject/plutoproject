package ink.pmc.common.exchange

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.common.exchange.backend.AbstractBackendExchangeService
import ink.pmc.common.exchange.backend.BackendExchangeService
import ink.pmc.common.exchange.lobby.ExchangeHandler
import ink.pmc.common.exchange.lobby.LobbyExchangeService
import ink.pmc.common.exchange.lobby.LogicDisabler
import ink.pmc.common.exchange.lobby.PlayerActionHandler
import ink.pmc.common.utils.isInDebugMode
import ink.pmc.common.utils.platform.paper
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.command.CommandSender
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import java.io.File
import java.util.logging.Level

lateinit var backendExchangeService: AbstractBackendExchangeService
lateinit var paperCommandManager: PaperCommandManager<CommandSender>
lateinit var world: World

@Suppress("UNUSED")
class PaperExchangePlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        if (isInDebugMode()) {
            return
        }

        serverLogger = logger
        dataDir = dataFolder
        createDataDir()
        configFile = File(dataDir, "config_backend.toml")

        if (!configFile.exists()) {
            saveResource("config_backend.toml", false)
        }

        loadConfig(configFile)

        paperCommandManager = PaperCommandManager.createNative(
            this,
            ExecutionCoordinator.asyncCoordinator()
        )
        paperCommandManager.registerBrigadier()

        when (fileConfig.get<Boolean>("lobby-mode")) {
            true -> initAsLobby()
            false -> initAsNormal()
        }

        disabled = false
    }

    override suspend fun onDisableAsync() {
        backendExchangeService.stopBackGroundJobs()
        disabled = true
    }

    private fun initAsNormal() {
        initService(BackendExchangeService())
    }

    private fun initAsLobby() {
        val name = lobbyWorldName
        serverLogger.info("Loading lobby world: $name")

        val tempWorld = try {
            Bukkit.createWorld(WorldCreator.name(name))
        } catch (e: Exception) {
            serverLogger.log(Level.SEVERE, "Failed to load lobby world", e)
            return
        }

        if (tempWorld == null) {
            serverLogger.severe("World loaded without exception, but it's null")
            return
        }

        world = tempWorld
        initService(LobbyExchangeService(world))
        paper.pluginManager.registerSuspendingEvents(LogicDisabler, this)
        paper.pluginManager.registerSuspendingEvents(ExchangeHandler, this)
        paper.pluginManager.registerSuspendingEvents(PlayerActionHandler, this)
    }

    private fun initService(service: AbstractBackendExchangeService) {
        backendExchangeService = service
        exchangeService = backendExchangeService
        IExchangeService.instance = exchangeService
    }

}