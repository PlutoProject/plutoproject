package ink.pmc.framework

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.framework.interactive.GuiListener
import ink.pmc.framework.interactive.GuiManagerImpl
import ink.pmc.framework.options.BackendOptionsUpdateNotifier
import ink.pmc.framework.options.OptionsUpdateNotifier
import ink.pmc.framework.options.listeners.BukkitOptionsListener
import ink.pmc.framework.options.startOptionsMonitor
import ink.pmc.framework.options.stopOptionsMonitor
import ink.pmc.framework.playerdb.BackendNotifier
import ink.pmc.framework.playerdb.Notifier
import ink.pmc.framework.playerdb.playerDbScope
import ink.pmc.framework.visual.display.text.TextDisplayFactoryImpl
import ink.pmc.framework.visual.display.text.TextDisplayListener
import ink.pmc.framework.visual.display.text.TextDisplayManagerImpl
import ink.pmc.framework.visual.display.text.renderers.NmsTextDisplayRenderer
import ink.pmc.framework.visual.toast.renderers.NmsToastRenderer
import ink.pmc.interactive.api.GuiManager
import ink.pmc.provider.Provider
import ink.pmc.rpc.api.RpcClient
import ink.pmc.utils.currentUnixTimestamp
import ink.pmc.utils.hook.initPaperHooks
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.jvm.loadClassesInPackages
import ink.pmc.utils.platform.paper
import ink.pmc.utils.platform.paperThread
import ink.pmc.utils.storage.saveResourceIfNotExisted
import ink.pmc.visual.api.display.text.TextDisplayFactory
import ink.pmc.visual.api.display.text.TextDisplayManager
import ink.pmc.visual.api.display.text.TextDisplayRenderer
import ink.pmc.visual.api.toast.ToastRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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
        single<Notifier> { BackendNotifier() }
        single<ToastRenderer<Player>>(named("internal")) { NmsToastRenderer() }
        single<TextDisplayManager> { TextDisplayManagerImpl() }
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
        paper.pluginManager.registerSuspendingEvents(GuiListener, frameworkPaper)
        paper.pluginManager.registerSuspendingEvents(BukkitOptionsListener, frameworkPaper)
        paper.pluginManager.registerSuspendingEvents(TextDisplayListener, frameworkPaper)
        startOptionsMonitor()
    }

    override suspend fun onEnableAsync() {
        initPaperHooks()
    }

    override suspend fun onDisableAsync() {
        GuiManager.disposeAll()
        playerDbScope.cancel()
        stopOptionsMonitor()
        withContext(Dispatchers.IO) {
            Provider.close()
        }
        RpcClient.stop()
    }

    private fun preload() {
        val start = currentUnixTimestamp
        frameworkLogger.info("Preloading Compose Runtime & Voyager to improve performance...")
        loadClassesInPackages("androidx", "cafe.adriel.voyager", classLoader = frameworkClassLoader)
        loadClassesInPackages("ink.pmc.interactive.api", classLoader = frameworkClassLoader)
        val end = currentUnixTimestamp
        frameworkLogger.info("Preloading finished, took ${end - start}ms")
    }
}