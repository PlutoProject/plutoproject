package ink.pmc.driftbottle

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import org.bukkit.plugin.Plugin
import org.koin.dsl.module
import java.util.logging.Logger

lateinit var pluginLogger: Logger
lateinit var plugin: Plugin

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {
    private val bukkitModule = module {

    }

    override suspend fun onEnableAsync() {
        plugin = this
        pluginLogger = logger
        startKoinIfNotPresent {
            modules(bukkitModule)
        }
    }

    override suspend fun onDisableAsync() {

    }
}