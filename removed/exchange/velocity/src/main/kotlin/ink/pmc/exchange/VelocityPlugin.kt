package ink.pmc.exchange

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.exchange.api.IExchangeService
import ink.pmc.exchange.commands.ProxyExchangeCommand
import ink.pmc.exchange.commands.ProxyTicketsCommand
import ink.pmc.framework.utils.command.init
import ink.pmc.framework.utils.platform.proxy
import ink.pmc.framework.utils.platform.saveConfig
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

lateinit var pluginContainer: PluginContainer
lateinit var proxyExchangeService: ProxyExchangeService
lateinit var velocityCommandManager: VelocityCommandManager<CommandSource>
lateinit var ticketDistributor: TicketDistributor

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun velocityExchangePlugin(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        serverLogger = logger
        dataDir = dataDirectoryPath.toFile()

        createDataDir()
        configFile = File(dataDir, "config_proxy.toml")

        if (!configFile.exists()) {
            saveConfig(VelocityPlugin::class.java, "config_proxy.toml", configFile)
        }

        loadConfig(configFile)
        initService()
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        pluginContainer = proxy.pluginManager.getPlugin("exchange").get()

        velocityCommandManager = VelocityCommandManager(
            pluginContainer,
            proxy,
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
        )

        velocityCommandManager.init(ProxyExchangeCommand)
        velocityCommandManager.init(ProxyTicketsCommand)

        ticketDistributor = TicketDistributor()

        proxy.eventManager.registerSuspend(this, ticketDistributor)
        proxy.eventManager.registerSuspend(this, ProxyExchangeHandler)

        disabled = false
    }

    @Subscribe
    suspend fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        ticketDistributor.stopDistJob()
        disabled = true
    }

    private fun initService() {
        proxyExchangeService = ProxyExchangeService()
        exchangeService = proxyExchangeService
        IExchangeService.instance = exchangeService
    }

}