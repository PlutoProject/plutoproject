package ink.pmc.provider

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.utils.platform.saveConfig
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
    }

    @Subscribe
    suspend fun proxyInitializationEvent(event: ProxyInitializeEvent) {
        val fileConfig = FileConfig.builder(configFile)
            .async()
            .autoreload()
            .build()

        fileConfig.load()
        fileConfig.loadProviderService()
    }
}