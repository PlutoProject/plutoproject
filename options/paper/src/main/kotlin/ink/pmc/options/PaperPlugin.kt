package ink.pmc.options

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.options.api.OptionsManager
import ink.pmc.options.listeners.BukkitOptionsListener
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.inject.startKoinIfNotPresent
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.koin.core.component.KoinComponent
import kotlin.time.Duration.Companion.minutes

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {
    override suspend fun onEnableAsync() {
        startKoinIfNotPresent {
            modules(commonModule)
        }
        server.pluginManager.registerSuspendingEvents(BukkitOptionsListener, this)
        startCleanerJob()
    }

    override suspend fun onDisableAsync() {
        stopCleanerJob()
    }

    private fun startCleanerJob() {
        cleanerJob = submitAsync {
            delay(10.minutes)
            // 复制一份防止 CME
            OptionsManager.loadedContainers.toList().forEach { c ->
                if (!Bukkit.getOnlinePlayers().any { it.uniqueId == c.owner }) {
                    OptionsManager.unloadContainer(c.owner)
                }
            }
        }
    }
}