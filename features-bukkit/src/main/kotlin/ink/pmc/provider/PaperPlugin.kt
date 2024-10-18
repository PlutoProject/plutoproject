package ink.pmc.provider

import com.electronwill.nightconfig.core.Config
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.utils.inject.startKoinIfNotPresent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    private val bukkitModule = module {
        single<Config>(named("provider_config")) { loadConfig() }
    }

    override suspend fun onLoadAsync() {
        startKoinIfNotPresent {
            modules(commonModule, bukkitModule)
        }
    }

    private fun loadConfig(): Config {
        val config = File(dataFolder, "config.conf")
        if (!config.exists()) {
            saveResource("config.conf", false)
        }
        return config.loadConfig()
    }

    override suspend fun onDisableAsync() {
        withContext(Dispatchers.IO) {
            ProviderService.close()
        }
    }

}