package ink.pmc.transfer

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.rpc.api.RpcClient
import ink.pmc.transfer.proto.TransferRpcGrpcKt.TransferRpcCoroutineStub
import ink.pmc.utils.platform.paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: JavaPlugin
lateinit var paperTransferService: AbstractTransferService

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        plugin = this
        val config = File(dataFolder, "config.conf")

        if (!config.exists()) {
            saveResource("config.conf", false)
        }

        config.loadConfig()

        paperTransferService = BackendTransferService(paper, TransferRpcCoroutineStub(RpcClient.channel), fileConfig)
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