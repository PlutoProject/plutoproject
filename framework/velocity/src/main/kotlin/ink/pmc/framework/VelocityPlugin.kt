package ink.pmc.framework

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
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
import ink.pmc.framework.options.proto.OptionsRpc
import ink.pmc.framework.playerdb.DatabaseNotifier
import ink.pmc.framework.playerdb.ProxyDatabaseNotifier
import ink.pmc.framework.playerdb.proto.PlayerDbRpc
import ink.pmc.provider.Provider
import ink.pmc.rpc.api.RpcServer
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.platform.proxy
import ink.pmc.utils.platform.proxyThread
import ink.pmc.utils.platform.saveDefaultConfig
import kotlinx.coroutines.Dispatchers
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
class VelocityPlugin @Inject constructor(private val spc: SuspendingPluginContainer) {
    private val velocityModule = module {
        single<File>(FRAMEWORK_CONFIG) { saveDefaultConfig(VelocityPlugin::class.java, dataFolder) }
        single<OptionsUpdateNotifier> { ProxyOptionsUpdateNotifier() }
        single<DatabaseNotifier> { ProxyDatabaseNotifier() }
    }
    private lateinit var dataFolder: File

    init {
        spc.initialize(this)
    }

    @Inject
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
        RpcServer.apply {
            addService(OptionsRpc)
            addService(PlayerDbRpc)
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
        withContext(Dispatchers.IO) {
            Provider.close()
        }
        RpcServer.stop()
        // gRPC 和数据库相关 IO 连接不会立马关闭
        // 可能导致在插件卸载之后，后台还有正在运行的 IO 操作
        // 若对应操作中加载了没有加载的类，而 framework 已经卸载，就会找不到类
        frameworkLogger.info("Waiting 1s for finalizing...")
        Thread.sleep(1000)
    }
}