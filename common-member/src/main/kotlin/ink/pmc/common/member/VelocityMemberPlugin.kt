package ink.pmc.common.member

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
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.logging.Logger

lateinit var proxyServer: ProxyServer
lateinit var proxyLogger: Logger
lateinit var pluginContainer: PluginContainer
lateinit var commandManager: VelocityCommandManager<CommandSource>

fun saveDefaultConfig(output: File) {
    val input: InputStream = VelocityMemberPlugin::class.java.getResourceAsStream("/config.toml")
        ?: throw IllegalArgumentException("Resource not found")
    Files.copy(input, output.toPath(), StandardCopyOption.REPLACE_EXISTING)
    input.close()
}

@Plugin(
    id = "common-member",
    name = "common-member",
    version = "1.0.0",
    dependencies = [Dependency(id = "common-dependency-loader-velocity"), Dependency(id = "common-utils")]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityMemberPlugin {

    @Inject
    fun memberPluginVelocity(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        proxyServer = server
        proxyLogger = logger
        dataDir = dataDirectoryPath.toFile()
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        pluginContainer = proxyServer.pluginManager.getPlugin("common-member").get()

        createDataDir()
        configFile = File(dataDir, "config.toml")

        if (!configFile.exists()) {
            saveDefaultConfig(configFile)
        }

        initMemberManager()

        commandManager = VelocityCommandManager(
            pluginContainer,
            proxyServer,
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
        )

        commandManager.command(memberAddCommand)
        commandManager.command(memberRemoveCommand)
        commandManager.command(memberLookupCommand)

        proxyServer.eventManager.register(this, VelocityPlayerListener)
    }

    @Subscribe
    fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        mongoClient.close()
    }

}