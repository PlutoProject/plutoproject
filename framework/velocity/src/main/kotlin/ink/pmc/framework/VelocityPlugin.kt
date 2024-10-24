package ink.pmc.framework

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.framework.commands.RpcCommand
import ink.pmc.framework.options.OptionsUpdateNotifier
import ink.pmc.framework.options.ProxyOptionsUpdateNotifier
import ink.pmc.framework.options.listeners.VelocityOptionsListener
import ink.pmc.framework.playerdb.Notifier
import ink.pmc.framework.playerdb.playerDbScope
import ink.pmc.framework.playerdb.ProxyNotifier
import ink.pmc.provider.Provider
import ink.pmc.rpc.api.RpcServer
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.platform.proxy
import ink.pmc.utils.platform.proxyThread
import ink.pmc.utils.platform.saveDefaultConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager
import org.koin.dsl.module
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

@Suppress("UNUSED", "UnusedReceiverParameter")
class VelocityPlugin(private val spc: SuspendingPluginContainer) {
    private val velocityModule = module {
        single<File>(FRAMEWORK_CONFIG) { saveDefaultConfig(VelocityPlugin::class.java, dataFolder) }
        single<OptionsUpdateNotifier> { ProxyOptionsUpdateNotifier() }
        single<Notifier> { ProxyNotifier() }
    }
    private lateinit var dataFolder: File

    init {
        spc.initialize(this)
    }

    fun framework(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        proxy = server
        frameworkLogger = logger
        proxyThread = Thread.currentThread()
        frameworkVelocity = spc.pluginContainer
        dataFolder = dataDirectoryPath.toFile().also {
            if (!it.exists()) it.mkdirs()
        }
        startKoinIfNotPresent {
            modules(commonModule, velocityModule)
        }
        server.eventManager.registerSuspend(this, VelocityOptionsListener)
    }

    @Subscribe
    fun ProxyInitializeEvent.e() {
        VelocityCommandManager(
            spc.pluginContainer,
            proxy,
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
        ).apply {
            AnnotationParser(this, CommandSource::class.java).apply {
                parse(RpcCommand)
            }
        }
        RpcServer.start()
    }

    @Subscribe
    suspend fun ProxyShutdownEvent.e() {
        playerDbScope.cancel()
        withContext(Dispatchers.IO) {
            Provider.close()
        }
        RpcServer.stop()
    }
}