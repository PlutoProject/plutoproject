package ink.pmc.transfer

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        plugin = this
        val config = File(dataFolder, "config.conf")

        if (!config.exists()) {
            saveResource("config.conf", false)
        }

        config.loadConfig()
    }

}