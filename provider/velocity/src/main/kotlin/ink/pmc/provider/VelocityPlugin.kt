package ink.pmc.provider

import com.electronwill.nightconfig.core.file.FileConfig
import com.electronwill.nightconfig.hocon.HoconFormat
import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.utils.platform.saveConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

lateinit var configFile: File

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun velocityProviderPlugin(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        val dataDir = dataDirectoryPath.toFile()

        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }

        configFile = File(dataDir, "config.conf")

        if (!configFile.exists()) {
            saveConfig(VelocityPlugin::class.java, "config.conf", configFile)
        }

        configFile.loadProviderService()
    }

    @Subscribe
    suspend fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        withContext(Dispatchers.IO) {
            providerService.close()
        }
    }
}