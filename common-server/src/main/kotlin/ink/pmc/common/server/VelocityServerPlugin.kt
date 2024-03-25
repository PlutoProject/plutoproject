package ink.pmc.common.server

import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.common.utils.platform.saveDefaultConfig
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

lateinit var proxyServer: ProxyServer
lateinit var proxyLogger: Logger
lateinit var pluginContainer: PluginContainer
lateinit var proxyCommandManager: VelocityCommandManager<CommandSource>

@Plugin(
    id = "common-server",
    name = "common-server",
    version = "1.0.1",
    dependencies = [Dependency(id = "common-dependency-loader-velocity"), Dependency(id = "common-dependency-loader-velocity")]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityServerPlugin {

    @Inject
    fun serverPluginVelocity(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        proxyServer = server
        proxyLogger = logger
        dataDir = dataDirectoryPath.toFile()
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        disabled = false
        pluginContainer = proxyServer.pluginManager.getPlugin("common-server").get()

        createDataDir()
        configFile = File(dataDir, "config.toml")

        if (!configFile.exists()) {
            saveDefaultConfig(VelocityServerPlugin::class.java, configFile)
        }

        loadConfig()

        proxyCommandManager = VelocityCommandManager(
            pluginContainer,
            proxyServer,
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
        )
    }

    @Subscribe
    fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        disabled = true
        serverService.close()
    }

}