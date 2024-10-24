package ink.pmc.framework

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.framework.interactive.GuiListener
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.interactive.GuiManagerImpl
import ink.pmc.framework.interactive.inventory.InventoryListener
import ink.pmc.framework.options.BackendOptionsUpdateNotifier
import ink.pmc.framework.options.OptionsUpdateNotifier
import ink.pmc.framework.options.listeners.BukkitOptionsListener
import ink.pmc.framework.options.startOptionsMonitor
import ink.pmc.framework.options.stopOptionsMonitor
import ink.pmc.framework.playerdb.BackendDatabaseNotifier
import ink.pmc.framework.playerdb.DatabaseNotifier
import ink.pmc.framework.playerdb.startPlayerDbMonitor
import ink.pmc.framework.playerdb.stopPlayerDbMonitor
import ink.pmc.framework.provider.Provider
import ink.pmc.framework.rpc.RpcClient
import ink.pmc.framework.utils.currentUnixTimestamp
import ink.pmc.framework.utils.hook.initPaperHooks
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import ink.pmc.framework.utils.jvm.loadClassesInPackages
import ink.pmc.framework.utils.platform.paper
import ink.pmc.framework.utils.platform.paperThread
import ink.pmc.framework.utils.storage.saveResourceIfNotExisted
import ink.pmc.framework.visual.display.text.*
import ink.pmc.framework.visual.display.text.renderers.NmsTextDisplayRenderer
import ink.pmc.framework.visual.toast.ToastRenderer
import ink.pmc.framework.visual.toast.renderers.NmsToastRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {
    private val config by inject<FrameworkConfig>()
    private val bukkitModule = module {
        single<File>(FRAMEWORK_CONFIG) { saveResourceIfNotExisted("config.conf") }
        single<GuiManager> { GuiManagerImpl() }
        single<OptionsUpdateNotifier> { BackendOptionsUpdateNotifier() }
        single<DatabaseNotifier> { BackendDatabaseNotifier() }
        single<ToastRenderer<Player>>(named("internal")) { NmsToastRenderer() }
        single<ink.pmc.framework.visual.display.text.TextDisplayManager> { TextDisplayManagerImpl() }
        single<TextDisplayFactory> { TextDisplayFactoryImpl() }
        single<TextDisplayRenderer>(named("internal")) { NmsTextDisplayRenderer() }
    }

    override fun onLoad() {
        frameworkLogger = logger
        paperThread = Thread.currentThread()
        this.server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        startKoinIfNotPresent {
            modules(commonModule, bukkitModule)
        }
        RpcClient.start()
        preload()
    }

    override suspend fun onEnableAsync() {
        paper.pluginManager.registerSuspendingEvents(GuiListener, frameworkPaper)
        paper.pluginManager.registerSuspendingEvents(InventoryListener, frameworkPaper)
        paper.pluginManager.registerSuspendingEvents(BukkitOptionsListener, frameworkPaper)
        paper.pluginManager.registerSuspendingEvents(TextDisplayListener, frameworkPaper)
        startPlayerDbMonitor()
        startOptionsMonitor()
        initPaperHooks()
    }

    override suspend fun onDisableAsync() {
        GuiManager.disposeAll()
        stopPlayerDbMonitor()
        stopOptionsMonitor()
        withContext(Dispatchers.IO) {
            Provider.close()
        }
        RpcClient.stop()
        // gRPC 和数据库相关 IO 连接不会立马关闭
        // 可能导致在插件卸载之后，后台还有正在运行的 IO 操作
        // 若对应操作中加载了没有加载的类，而 framework 已经卸载，就会找不到类
        logger.info("Waiting 1s for finalizing...")
        Thread.sleep(1000)
    }

    private fun preload() {
        val start = currentUnixTimestamp
        frameworkLogger.info("Preloading necessary classes to improve performance...")
        loadClassesInPackages(
            "androidx",
            "cafe.adriel.voyager",
            "ink.pmc.framework",
            classLoader = frameworkClassLoader
        )
        val end = currentUnixTimestamp
        frameworkLogger.info("Preloading finished, took ${end - start}ms")
    }
}