package ink.pmc.common.rpc

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
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
import ink.pmc.common.rpc.commands.RpcServerCommand
import ink.pmc.common.rpc.impl.RpcServerImpl
import ink.pmc.common.utils.PLUTO_VERSION
import ink.pmc.common.utils.command.init
import ink.pmc.common.utils.platform.proxy
import ink.pmc.common.utils.platform.saveConfig
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

lateinit var pluginContainer: PluginContainer
lateinit var velocityCommandManager: VelocityCommandManager<CommandSource>

lateinit var rpcServer: RpcServerImpl

@Plugin(
    id = "common-rpc",
    name = "common-rpc",
    version = PLUTO_VERSION,
    dependencies = [
        Dependency(id = "common-dependency-loader-velocity"),
        Dependency(id = "common-utils")
    ]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityRpcPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun velocityRpcPlugin(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        serverLogger = logger
        dataDir = dataDirectoryPath.toFile()

        createDataDir()
        loadConfig()

        rpcServer = RpcServerImpl(fileConfig.get("port"))
        IRpcServer.instance = rpcServer
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        pluginContainer = proxy.pluginManager.getPlugin("common-rpc").get()

        velocityCommandManager = VelocityCommandManager(
            pluginContainer,
            proxy,
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
        )

        velocityCommandManager.init(RpcServerCommand)

        rpcServer.start()
        disabled = false
    }

    @Subscribe
    fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        rpcServer.close()
        disabled = true
    }

    private fun loadConfig() {
        configFile = File(dataDir, "config_server.toml")

        if (!configFile.exists()) {
            saveConfig(VelocityRpcPlugin::class.java, "config_server.toml", configFile)
        }

        loadConfig(configFile)
    }

}