package ink.pmc.common.exchange

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.common.exchange.paper.BackendExchangeService
import ink.pmc.common.utils.isInDebugMode
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.command.CommandSender
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import java.io.File
import java.util.logging.Level

lateinit var backendExchangeService: BackendExchangeService
lateinit var paperCommandManager: PaperCommandManager<CommandSender>
lateinit var world: World

@Suppress("UNUSED")
class PaperExchangePlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        if (isInDebugMode()) {
            return
        }

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
        disabled = true
    }

    private fun initAsNormal() {

    }

    private fun initAsLobby() {
        val name = fileConfig.get<String>("lobby-settings.world")
        serverLogger.info("Loading lobby world: $name")

        val tempWorld = try {
            Bukkit.createWorld(WorldCreator.name(name))
        } catch (e: Exception) {
            serverLogger.log(Level.SEVERE, "Failed to load lobby world!", e)
            return
        }

        if (tempWorld == null) {
            serverLogger.severe("World loaded without exception, but it's null")
            return
        }

        world = tempWorld
    }

}