package ink.pmc.common.member

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class MemberPlugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        disabled = false

        dataDir = dataFolder
        configFile = File(dataFolder, "config.yml")

        if (!configFile.exists()) {
            saveDefaultConfig()
        }

        initMemberManager()
    }

    override fun onDisable() {
        disabled = true
        mongoClient.close()
    }

}