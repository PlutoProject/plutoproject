package ink.pmc.provider

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        val config = File(dataFolder, "config.conf")

        if (!config.exists()) {
            saveResource("config.conf", false)
        }

        val fileConfig = FileConfig.builder(file)
            .async()
            .autoreload()
            .build()

        fileConfig.load()
        fileConfig.loadProviderService()
    }

    override suspend fun onDisableAsync() {
        withContext(Dispatchers.IO) {
            providerService.close()
        }
    }

}