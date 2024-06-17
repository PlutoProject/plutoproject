package ink.pmc.transfer

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: JavaPlugin
lateinit var paperTransferService: PaperTransferService

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        plugin = this
        val config = File(dataFolder, "config.conf")

        if (!config.exists()) {
            saveResource("config.conf", false)
        }

        config.loadConfig()

        paperTransferService = PaperTransferService()
        transferService = paperTransferService

        disabled = false
    }

    override suspend fun onDisableAsync() {
        disabled = true
        withContext(Dispatchers.IO) {
            transferService.close()
        }
    }

}