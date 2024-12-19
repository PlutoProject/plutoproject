package ink.pmc.framework

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.proxy.BridgeCommand
import ink.pmc.framework.bridge.proxy.BridgeRpc
import ink.pmc.framework.bridge.proxy.ProxyBridge
import ink.pmc.framework.bridge.proxy.listeners.BridgePlayerListener
import ink.pmc.framework.commands.RpcCommand
import ink.pmc.framework.options.OptionsUpdateNotifier
import ink.pmc.framework.options.ProxyOptionsUpdateNotifier
import ink.pmc.framework.options.listeners.VelocityOptionsListener
import ink.pmc.framework.options.proto.OptionsRpc
import ink.pmc.framework.playerdb.DatabaseNotifier
import ink.pmc.framework.playerdb.ProxyDatabaseNotifier
import ink.pmc.framework.playerdb.proto.PlayerDbRpc
import ink.pmc.framework.provider.Provider
import ink.pmc.framework.rpc.RpcServer
import ink.pmc.framework.command.annotationParser
import ink.pmc.framework.command.commandManager
import ink.pmc.framework.concurrent.cancelFrameworkScopes
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import ink.pmc.framework.utils.platform.proxy
import ink.pmc.framework.utils.platform.proxyThread
import ink.pmc.framework.utils.platform.saveDefaultConfig
import ink.pmc.framework.utils.player.profile.ProfileCacheListener
import net.kyori.adventure.text.minimessage.MiniMessage
import org.incendo.cloud.minecraft.extras.parser.ComponentParser
import org.incendo.cloud.parser.standard.StringParser
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
        single<Bridge> { ProxyBridge() }
    }
    private lateinit var dataFolder: File

    init {
        spc.initialize(this)
    }

    @Inject
    fun framework(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        proxy = server
        frameworkLogger = logger
        frameworkDataFolder = dataDirectoryPath.toFile().apply { if (!exists()) mkdirs() }
        proxyThread = Thread.currentThread()
        frameworkVelocity = spc.pluginContainer
        dataFolder = dataDirectoryPath.toFile().also {
            if (!it.exists()) it.mkdirs()
        }
        startKoinIfNotPresent {
            modules(commonModule, velocityModule)
        }
        Provider // 初始化
        RpcServer.apply {
            addService(OptionsRpc)
            addService(PlayerDbRpc)
            addService(BridgeRpc)
        }
        server.eventManager.registerSuspend(this, VelocityOptionsListener)
        server.eventManager.registerSuspend(this, BridgePlayerListener)
        server.eventManager.registerSuspend(this, ProfileCacheListener)
    }

    @Subscribe
    fun ProxyInitializeEvent.e() {
        spc.commandManager().apply {
            parserRegistry().apply {
                registerNamedParser(
                    "bridge-component",
                    ComponentParser.componentParser(MiniMessage.miniMessage(), StringParser.StringMode.QUOTED)
                )
            }
        }.annotationParser().apply {
            parse(RpcCommand)
            parse(BridgeCommand)
        }
        RpcServer.start()
    }

    @Subscribe
    fun ProxyShutdownEvent.e() {
        Provider.close()
        RpcServer.stop()
        // gRPC 和数据库相关 IO 连接不会立马关闭
        // 可能导致在插件卸载之后，后台还有正在运行的 IO 操作
        // 若对应操作中加载了没有加载的类，而 framework 已经卸载，就会找不到类
        frameworkLogger.info("Waiting 1s for finalizing...")
        Thread.sleep(1000)
        cancelFrameworkScopes()
    }
}