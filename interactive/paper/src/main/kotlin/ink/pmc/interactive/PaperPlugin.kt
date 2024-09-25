package ink.pmc.interactive

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.interactive.api.Gui
import ink.pmc.interactive.inventory.InventoryListener
import ink.pmc.utils.PaperCm
import ink.pmc.utils.inject.startKoinIfNotPresent
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

internal lateinit var plugin: JavaPlugin

private lateinit var commandManager: PaperCm
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
        commandManager = PaperCm.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)
            .apply { interactive(arrayOf()) }
        server.pluginManager.registerSuspendingEvents(GuiListener, this)
        server.pluginManager.registerSuspendingEvents(InventoryListener, this)
    }

    override suspend fun onDisableAsync() {
        gui.disposeAll()
    }

}