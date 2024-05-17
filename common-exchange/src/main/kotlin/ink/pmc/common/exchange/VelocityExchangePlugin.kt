package ink.pmc.common.exchange

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
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
import ink.pmc.common.exchange.commands.TicketsCommand
import ink.pmc.common.exchange.listeners.VelocityExchangeServiceListener
import ink.pmc.common.exchange.velocity.VelocityExchangeService
import ink.pmc.common.utils.PLUTO_VERSION
import ink.pmc.common.utils.command.init
import ink.pmc.common.utils.platform.proxy
import ink.pmc.common.utils.platform.saveDefaultConfig
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

lateinit var pluginContainer: PluginContainer
lateinit var velocityCommandManager: VelocityCommandManager<CommandSource>

@Plugin(
    id = "common-exchange",
    name = "common-exchange",
    version = PLUTO_VERSION,
    dependencies = [
        Dependency(id = "common-dependency-loader-velocity"),
        Dependency(id = "common-utils"),
        Dependency(id = "common-member"),
        Dependency(id = "common-rpc")
    ]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityExchangePlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun exchangePluginVelocity(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        serverLogger = logger
        dataDir = dataDirectoryPath.toFile()
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        pluginContainer = proxy.pluginManager.getPlugin("common-member").get()

        createDataDir()
        configFile = File(dataDir, "config.toml")

        if (!configFile.exists()) {
            saveDefaultConfig(VelocityExchangePlugin::class.java, configFile)
        }

        velocityCommandManager = VelocityCommandManager(
            pluginContainer,
            proxy,
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
        )

        initService()
        velocityCommandManager.init(TicketsCommand)
        proxy.eventManager.registerSuspend(this, VelocityExchangeServiceListener)
        disabled = false
    }

    @Subscribe
    fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        disabled = true
    }

    private fun initService() {
        exchangeService = VelocityExchangeService()
        IExchangeService.instance = exchangeService
    }

}