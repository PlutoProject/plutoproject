package ink.pmc.serverselector

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.sksamuel.hoplite.PropertySource
import ink.pmc.framework.options.OptionsManager
import ink.pmc.framework.utils.config.preconfiguredConfigLoaderBuilder
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import ink.pmc.framework.utils.storage.saveResourceIfNotExisted
import ink.pmc.serverselector.listener.LobbyListener
import ink.pmc.serverselector.listener.TimeSyncListener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.module

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {
    private val bukkitModule = module {
        single<ServerSelectorConfig> {
            preconfiguredConfigLoaderBuilder()
                .addPropertySource(PropertySource.file(saveResourceIfNotExisted("config.conf")))
                .build()
                .loadConfigOrThrow()
        }
    }

    override suspend fun onEnableAsync() {
        plugin = this
        startKoinIfNotPresent {
            modules(sharedModule, bukkitModule)
        }
        loadLobbyWorld()
        OptionsManager.registerOptionDescriptor(AUTO_JOIN_DESCRIPTOR)
        server.pluginManager.registerEvents(LobbyListener, this)
        server.pluginManager.registerEvents(TimeSyncListener, this)
    }

    override fun onDisable() {
        stopTimeSyncJobs()
    }
}