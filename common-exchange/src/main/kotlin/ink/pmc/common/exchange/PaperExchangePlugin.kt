package ink.pmc.common.exchange

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.common.exchange.paper.PaperExchangeService
import ink.pmc.common.utils.isInDebugMode
import java.io.File

lateinit var paperExchangeService: PaperExchangeService

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