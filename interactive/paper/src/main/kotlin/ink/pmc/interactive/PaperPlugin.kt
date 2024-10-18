package ink.pmc.interactive

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.interactive.api.GuiManager
import ink.pmc.interactive.inventory.InventoryListener
import ink.pmc.utils.PaperCm
import ink.pmc.utils.currentUnixTimestamp
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.jvm.loadClassesInPackages
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

internal lateinit var plugin: JavaPlugin

private lateinit var commandManager: PaperCm
private val bukkitModule = module {
    single<GuiManager> { GuiManagerImpl() }
}

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {

    private val guiManager by inject<GuiManager>()

    override suspend fun onLoadAsync() {
        preload()
    }

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
        guiManager.disposeAll()
    }

    private fun preload() {
        val start = currentUnixTimestamp
        val depClassLoader = Class.forName("ink.pmc.deploader.PaperPlugin").classLoader
        val classLoader = this.classLoader
        logger.info("Preloading Compose Runtime & Voyager to improve performance...")
        loadClassesInPackages("androidx", "cafe.adriel.voyager", classLoader = depClassLoader)
        loadClassesInPackages("ink.pmc.interactive.api", classLoader = classLoader)
        val end = currentUnixTimestamp
        logger.info("Preloading finished, took ${end - start}ms")
    }

}