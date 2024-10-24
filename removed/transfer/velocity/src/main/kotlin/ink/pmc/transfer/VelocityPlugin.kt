package ink.pmc.transfer

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.provider.ProviderService
import ink.pmc.rpc.api.RpcServer
import ink.pmc.transfer.proxy.AbstractProxyTransferService
import ink.pmc.transfer.proxy.ProxyTransferService
import ink.pmc.transfer.proxy.commands.TransferCommand
import ink.pmc.framework.utils.command.init
import ink.pmc.framework.utils.platform.proxy
import ink.pmc.framework.utils.platform.saveConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.host.toScriptSource

lateinit var pluginContainer: PluginContainer
lateinit var velocityCommandManager: VelocityCommandManager<CommandSource>
lateinit var proxyTransferService: AbstractProxyTransferService

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun velocityTransfer(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        serverLogger = logger
        dataDir = dataDirectoryPath.toFile()

        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }

        val config = File(dataDir, "config.conf")

        if (!config.exists()) {
            saveConfig(VelocityPlugin::class.java, "config.conf", config)
        }

        config.loadConfig()

        proxyTransferService = ProxyTransferService(
            server,
            RpcServer,
            fileConfig,
            ProviderService.defaultMongoDatabase,
            loadScript()
        )
        transferService = proxyTransferService
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        pluginContainer = proxy.pluginManager.getPlugin("transfer").get()

        velocityCommandManager = VelocityCommandManager(
            pluginContainer,
            proxy,
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
        )

        velocityCommandManager.init(TransferCommand(proxyTransferService))
        disabled = false
    }

    @Subscribe
    suspend fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        disabled = true
        withContext(Dispatchers.IO) {
            transferService.close()
        }
    }

    private fun loadScript(): SourceCode {
        val file = File(dataDir, "config.proxy.kts")

        if (!file.exists()) {
            saveConfig(VelocityPlugin::class.java, "config.proxy.kts", file)
        }

        return file.toScriptSource()
    }

}