package ink.pmc.driftbottle

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import org.koin.dsl.module
import java.util.logging.Logger

lateinit var pluginLogger: Logger

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {
    private val bukkitModule = module {

    }

    override suspend fun onEnableAsync() {
        pluginLogger = logger
        startKoinIfNotPresent {
            modules(bukkitModule)
        }
    }

    override suspend fun onDisableAsync() {

    }
}