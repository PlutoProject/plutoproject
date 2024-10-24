package ink.pmc.framework

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.utils.hook.initPaperHooks
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.platform.paperThread
import ink.pmc.utils.storage.saveResourceIfNotExisted
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import java.io.File

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {
    private val config by inject<FrameworkConfig>()
    private val bukkitModule = module {
        single<File>(FRAMEWORK_CONFIG) { saveResourceIfNotExisted("config.conf") }
    }

    override fun onLoad() {
        frameworkLogger = logger
        paperThread = Thread.currentThread()
        this.server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        startKoinIfNotPresent {
            modules(commonModule, bukkitModule)
        }
    }

    override suspend fun onEnableAsync() {
        initPaperHooks()
    }
}