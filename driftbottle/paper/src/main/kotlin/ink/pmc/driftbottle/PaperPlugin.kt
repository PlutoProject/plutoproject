package ink.pmc.driftbottle

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.driftbottle.api.BottleFactory
import ink.pmc.driftbottle.api.BottleManager
import ink.pmc.driftbottle.repositories.BottleRepository
import ink.pmc.framework.provider.Provider
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import org.bukkit.plugin.Plugin
import org.koin.dsl.module
import java.util.logging.Logger

lateinit var pluginLogger: Logger
lateinit var plugin: Plugin

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {
    private val bukkitModule = module {
        single<BottleRepository> { BottleRepository(Provider.defaultMongoDatabase.getCollection("driftbottle_data")) }
        single<BottleManager> { BottleManagerImpl() }
        single<BottleFactory> { BottleFactoryImpl() }
    }

    override suspend fun onEnableAsync() {
        plugin = this
        pluginLogger = logger
        startKoinIfNotPresent {
            modules(bukkitModule)
        }
    }

    override suspend fun onDisableAsync() {

    }
}