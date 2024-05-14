package ink.pmc.common.exchange

import com.electronwill.nightconfig.core.Config
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.common.exchange.commands.CheckoutCommand
import ink.pmc.common.exchange.commands.ExchangeCommand
import ink.pmc.common.exchange.listeners.PaperExchangeServiceListener
import ink.pmc.common.exchange.paper.ExchangeLobbyImpl
import ink.pmc.common.exchange.paper.ExchangeWorldLoader
import ink.pmc.common.exchange.paper.PaperExchangeService
import ink.pmc.common.exchange.serializers.*
import ink.pmc.common.exchange.utils.disableGameRules
import ink.pmc.common.utils.command.init
import ink.pmc.common.utils.isInDebugMode
import ink.pmc.common.utils.json.transformGson
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.inventory.Inventory
import org.bukkit.potion.PotionEffect
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import java.io.File

lateinit var paperExchangeService: PaperExchangeService
lateinit var paperCommandManager: PaperCommandManager<CommandSender>
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

            disableGameRules(world)
            logger.info("World loaded!")
        } catch (e: Exception) {
            logger.severe("Failed to load world!")
            e.printStackTrace()
            return
        }

        paperCommandManager = PaperCommandManager.createNative(
            this,
            ExecutionCoordinator.asyncCoordinator()
        )

        paperCommandManager.registerBrigadier()
        paperCommandManager.init(ExchangeCommand)
        paperCommandManager.init(CheckoutCommand)

        transformGson {
            registerTypeAdapter(Location::class.java, LocationSerializer)
            registerTypeAdapter(Location::class.java, LocationDeserializer)
            registerTypeAdapter(PotionEffect::class.java, PotionEffectSerializer)
            registerTypeAdapter(PotionEffect::class.java, PotionEffectDeserializer)
            registerTypeAdapter(Inventory::class.java, InventorySerializer)
            registerTypeAdapter(Inventory::class.java, InventoryDeserializer)
        }

        initService(exchangeLobby)
        server.pluginManager.registerSuspendingEvents(PaperExchangeServiceListener, this)
        disabled = false
    }

    override suspend fun onDisableAsync() {
        disabled = true
    }

    private fun initService(exchangeLobby: ExchangeLobby) {
        paperExchangeService = PaperExchangeService(exchangeLobby)
        exchangeService = paperExchangeService
        IExchangeService.instance = exchangeService
    }

    private fun loadConfigPaper(config: Config) {
        ExchangeConfig.ExchangeLobby.worldName = config.get("exchange-lobby.world-name")

        ExchangeConfig.ExchangeLobby.TeleportLocation.x = config.get("exchange-lobby.teleport-location.x")
        ExchangeConfig.ExchangeLobby.TeleportLocation.y = config.get("exchange-lobby.teleport-location.y")
        ExchangeConfig.ExchangeLobby.TeleportLocation.z = config.get("exchange-lobby.teleport-location.z")
        ExchangeConfig.ExchangeLobby.TeleportLocation.yaw = config.get("exchange-lobby.teleport-location.yaw")
        ExchangeConfig.ExchangeLobby.TeleportLocation.pitch = config.get("exchange-lobby.teleport-location.pitch")

        ExchangeConfig.AvailableItems.materials = processMaterials(config.get("available-items.materials"))
    }

    private fun processMaterials(list: List<String>): List<Material> {
        return list.map { Material.valueOf(it.uppercase()) }
    }

}