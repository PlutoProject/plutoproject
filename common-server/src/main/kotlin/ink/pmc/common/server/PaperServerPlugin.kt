package ink.pmc.common.server

import ink.pmc.common.utils.isInDebugMode
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class PaperServerPlugin : JavaPlugin() {

    override fun onEnable() {
        if (isInDebugMode()) {
            return
        }

        plugin = this
        disabled = false
        dataDir = dataFolder
        pluginLogger = logger

        createDataDir()
        configFile = File(dataFolder, "config.toml")

        if (!configFile.exists()) {
            saveResource("config.toml", false)
        }

        loadConfig()
    }

    override fun onDisable() {
        if (isInDebugMode()) {
            return
        }

        serverService.close()
        disabled = true
    }

}