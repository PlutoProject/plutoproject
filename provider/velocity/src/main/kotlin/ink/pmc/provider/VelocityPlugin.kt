package ink.pmc.provider

import com.electronwill.nightconfig.core.Config
import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.platform.saveConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

private lateinit var dataDir: File

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    private val velocityModule = module {
        single<Config>(named("provider_config")) { loadConfig() }
    }

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun velocityProviderPlugin(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        dataDir = dataDirectoryPath.toFile()
        startKoinIfNotPresent {
            modules(commonModule, velocityModule)
        }
    }

    private fun loadConfig(): Config {
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
        val config = File(dataDir, "config.conf")
        if (!config.exists()) {
            saveConfig(VelocityPlugin::class.java, "config.conf", config)
        }
        return config.loadConfig()
    }

    @Subscribe
    suspend fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        withContext(Dispatchers.IO) {
            ProviderService.close()
        }
    }

}