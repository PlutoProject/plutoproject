package ink.pmc.common.member

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class PaperMemberPlugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        disabled = false
        dataDir = dataFolder

        createDataDir()
        configFile = File(dataFolder, "config.toml")

        if (!configFile.exists()) {
            saveResource("config.toml", false)
        }

        if (!ink.pmc.common.member.isEnabled()) {
            return
        }

        initMemberManager()
    }

    override fun onDisable() {
        disabled = true
        mongoClient.close()
    }

}