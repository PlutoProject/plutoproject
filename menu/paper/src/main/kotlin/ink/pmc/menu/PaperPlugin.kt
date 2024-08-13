package ink.pmc.menu

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        plugin = this
    }

}