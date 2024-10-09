package ink.pmc.options

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.options.listeners.BukkitOptionsListener
import ink.pmc.utils.inject.startKoinIfNotPresent
import org.koin.core.component.KoinComponent

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {
    override suspend fun onEnableAsync() {
        startKoinIfNotPresent {
            modules(commonModule)
        }
        server.pluginManager.registerSuspendingEvents(BukkitOptionsListener, this)
    }

    override suspend fun onDisableAsync() {
    }
}