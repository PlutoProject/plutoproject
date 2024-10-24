package ink.pmc.transfer

import com.electronwill.nightconfig.core.Config
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.rpc.api.RpcClient
import ink.pmc.transfer.backend.BackendTransferService
import ink.pmc.transfer.backend.lobby.TransferLobby
import ink.pmc.transfer.proto.TransferRpcGrpcKt.TransferRpcCoroutineStub
import ink.pmc.framework.utils.platform.paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.host.toScriptSource

lateinit var plugin: JavaPlugin
lateinit var paperTransferService: AbstractTransferService
lateinit var backendSettings: Config
lateinit var transferLobby: TransferLobby

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        plugin = this
        dataDir = dataFolder
        serverLogger = logger
        val config = File(dataFolder, "config.conf")

        if (!config.exists()) {
            saveResource("config.conf", false)
        }

        config.loadConfig()

        backendSettings = fileConfig.get("backend-settings")
        paperTransferService = BackendTransferService(paper, TransferRpcCoroutineStub(RpcClient.channel), backendSettings)
        transferService = paperTransferService
        initLobby()

        disabled = false
    }

    override suspend fun onDisableAsync() {
        disabled = true

        if (::transferLobby.isInitialized) {
            transferLobby.destroy()
        }

        withContext(Dispatchers.IO) {
            transferService.close()
        }

        Bukkit.getScheduler().cancelTasks(this)
    }

    private fun initLobby() {
        if (backendSettings.get<String>("id") != "_transfer") {
            return
        }

        transferLobby = TransferLobby(paperTransferService, fileConfig.get("lobby-settings"), loadScript())
        server.pluginManager.registerSuspendingEvents(transferLobby.listener, this)
        server.pluginManager.registerSuspendingEvents(transferLobby.portalManager.bounding.listener, this)
        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            transferLobby.tick()
        }, 0L, 0L)
    }

    private fun loadScript(): SourceCode {
        val file = File(dataDir, "config.lobby.kts")

        if (!file.exists()) {
            saveResource("config.lobby.kts", false)
        }

        return file.toScriptSource()
    }

}