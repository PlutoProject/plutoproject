package ink.pmc.framework

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.framework.interactive.GuiListener
import ink.pmc.framework.interactive.GuiManagerImpl
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import java.io.File

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {
    private val config by inject<FrameworkConfig>()
    private val bukkitModule = module {
        single<File>(FRAMEWORK_CONFIG) { saveResourceIfNotExisted("config.conf") }
        single<GuiManager> { GuiManagerImpl() }
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
    }

    override suspend fun onEnableAsync() {
        initPaperHooks()
    }

    override suspend fun onDisableAsync() {
        GuiManager.disposeAll()
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