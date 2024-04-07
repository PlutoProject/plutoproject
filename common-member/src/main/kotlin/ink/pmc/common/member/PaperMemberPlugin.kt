package ink.pmc.common.member

import ink.pmc.common.utils.isInDebugMode
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class PaperMemberPlugin : JavaPlugin() {

    override fun onEnable() {
        if (isInDebugMode()) {
            return
        }

        plugin = this
        dataDir = dataFolder

        createDataDir()
        configFile = File(dataFolder, "config.toml")

        if (!configFile.exists()) {
            saveResource("config.toml", false)
        }

        initMemberManager()
        disabled = false
    }

    override fun onDisable() {
        if (isInDebugMode()) {
            return
        }

        safeDisable()
        disabled = true
    }

}