package ink.pmc.common.exchange

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.common.exchange.paper.ExchangeWorldLoader
import ink.pmc.common.exchange.paper.PaperExchangeService
import ink.pmc.common.utils.isInDebugMode
import org.bukkit.World
import java.io.File

lateinit var paperExchangeService: PaperExchangeService
lateinit var worldFolder: File
lateinit var worldLoader: ExchangeWorldLoader
lateinit var world: World

@Suppress("UNUSED")
class PaperExchangePlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        if (isInDebugMode()) {
            return
        }

        createDataDir()
        configFile = File(dataFolder, "config.toml")

        if (!configFile.exists()) {
            saveResource("config.toml", false)
        }

        worldFolder = File(dataFolder, "${ExchangeConfig.ExchangeLobby.worldName}/")

        if (!worldFolder.exists() || !worldFolder.isDirectory) {
            logger.severe("World folder '${ExchangeConfig.ExchangeLobby.worldName}' not existed or not a folder!")
            return
        }

        try {
            worldLoader = ExchangeWorldLoader(worldFolder)
            worldLoader.load()
            world = worldLoader.world
        } catch (e: Exception) {
            logger.severe("Failed to load world!")
            e.printStackTrace()
            return
        }

        loadConfig(configFile)
        initService()
        disabled = false
    }

    override suspend fun onDisableAsync() {
        disabled = true
    }

    private fun initService() {
        paperExchangeService = PaperExchangeService()
        exchangeService = paperExchangeService
    }

}