package ink.pmc.interactive

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.interactive.api.Interactive
import ink.pmc.interactive.api.inventory.canvas.InvListener
import ink.pmc.utils.inject.startKoinIfNotPresent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

val interactiveScope = CoroutineScope(Dispatchers.Default)

private val bukkitModule = module {
    single<Interactive> { InteractiveImpl() }
}

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {

    private val interactive by inject<Interactive>()

    override suspend fun onEnableAsync() {
        startKoinIfNotPresent {
            modules(bukkitModule)
        }
        interactive // 初始化
        server.pluginManager.registerSuspendingEvents(InvListener, this)
    }

    override suspend fun onDisableAsync() {
        interactive.dispose()
        interactiveScope.cancel()
    }

}