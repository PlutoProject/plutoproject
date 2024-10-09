package ink.pmc.options

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.options.listeners.BukkitOptionsListener
import ink.pmc.utils.inject.startKoinIfNotPresent
import org.koin.core.component.KoinComponent
import org.koin.dsl.module

private val bukkitModule = module {
    single<OptionsUpdateNotifier> { BackendOptionsUpdateNotifier() }
}

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {
    override suspend fun onEnableAsync() {
        pluginLogger = logger
        startKoinIfNotPresent {
            modules(commonModule, bukkitModule)
        }
        server.pluginManager.registerSuspendingEvents(BukkitOptionsListener, this)
        startMonitorContainerUpdate()
    }

    override suspend fun onDisableAsync() {
        stopMonitorContainerUpdate()
    }
}