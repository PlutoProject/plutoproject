package ink.pmc.common.exchange

import com.electronwill.nightconfig.core.Config
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.common.exchange.paper.ExchangeLobbyImpl
import ink.pmc.common.exchange.paper.ExchangeWorldLoader
import ink.pmc.common.exchange.paper.PaperExchangeService
import ink.pmc.common.exchange.paper.PaperPlayerListener
import ink.pmc.common.utils.isInDebugMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.type.Stairs
import java.io.File

lateinit var paperExchangeService: PaperExchangeService
lateinit var worldFolder: File
lateinit var worldLoader: ExchangeWorldLoader
lateinit var world: World
lateinit var exchangeLobby: ExchangeLobby

@Suppress("UNUSED")
class PaperExchangePlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        if (isInDebugMode()) {
            return
        }

        dataDir = dataFolder
        createDataDir()
        configFile = File(dataDir, "config.toml")

        if (!configFile.exists()) {
            saveResource("config.toml", false)
        }

        loadConfig(configFile)
        loadConfigPaper(fileConfig)
        worldFolder = File("${ExchangeConfig.ExchangeLobby.worldName}/")

        if (!worldFolder.exists() || !worldFolder.isDirectory) {
            logger.severe("World folder '${ExchangeConfig.ExchangeLobby.worldName}' not existed or not a folder!")
            return
        }

        try {
            logger.info("Loading exchange lobby...")

            worldLoader = ExchangeWorldLoader()
            worldLoader.load()
            world = worldLoader.world

            exchangeLobby = ExchangeLobbyImpl(
                world,
                Location(
                    world,
                    ExchangeConfig.ExchangeLobby.TeleportLocation.x,
                    ExchangeConfig.ExchangeLobby.TeleportLocation.y,
                    ExchangeConfig.ExchangeLobby.TeleportLocation.z,
                    ExchangeConfig.ExchangeLobby.TeleportLocation.yaw,
                    ExchangeConfig.ExchangeLobby.TeleportLocation.pitch
                )
            )

            logger.info("World loaded!")
        } catch (e: Exception) {
            logger.severe("Failed to load world!")
            e.printStackTrace()
            return
        }

        initService(exchangeLobby)
        server.pluginManager.registerSuspendingEvents(PaperPlayerListener, this)
        disabled = false
    }

    override suspend fun onDisableAsync() {
        disabled = true
    }

    private fun initService(exchangeLobby: ExchangeLobby) {
        paperExchangeService = PaperExchangeService(exchangeLobby)
        exchangeService = paperExchangeService
    }

    private fun loadConfigPaper(config: Config) {
        ExchangeConfig.ExchangeLobby.worldName = config.get("exchange-lobby.world-name")

        ExchangeConfig.ExchangeLobby.TeleportLocation.x = config.get("exchange-lobby.teleport-location.x")
        ExchangeConfig.ExchangeLobby.TeleportLocation.y = config.get("exchange-lobby.teleport-location.y")
        ExchangeConfig.ExchangeLobby.TeleportLocation.z = config.get("exchange-lobby.teleport-location.z")
        ExchangeConfig.ExchangeLobby.TeleportLocation.yaw = config.get("exchange-lobby.teleport-location.yaw")
        ExchangeConfig.ExchangeLobby.TeleportLocation.pitch = config.get("exchange-lobby.teleport-location.pitch")

        ExchangeConfig.Tickets.daily = config.get("tickets.daily")

        ExchangeConfig.AvailableItems.materials = processMaterials(config.get("available-items.materials"))
    }

    private fun processMaterials(list: List<String>): List<Material> {
        return list.map { Material.valueOf(it.uppercase()) }
    }

}