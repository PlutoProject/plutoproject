package ink.pmc.interactive

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.interactive.api.Gui
import ink.pmc.interactive.inventory.InventoryListener
import ink.pmc.utils.inject.startKoinIfNotPresent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

val interactiveScope = CoroutineScope(Dispatchers.Default)
internal lateinit var plugin: JavaPlugin

private val bukkitModule = module {
    single<Gui> { GuiImpl() }
}

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {

    private val gui by inject<Gui>()

    override suspend fun onEnableAsync() {
        plugin = this
        startKoinIfNotPresent {
            modules(bukkitModule)
        }
        server.pluginManager.registerSuspendingEvents(InventoryListener, this)
    }

    override suspend fun onDisableAsync() {
        gui.disposeAll()
        interactiveScope.cancel()
    }

}