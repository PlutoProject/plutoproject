package ink.pmc.common.rpc

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.common.rpc.event.RpcClientConnectEvent
import ink.pmc.common.rpc.event.RpcClientDisconnectEvent
import ink.pmc.common.rpc.impl.RpcClientImpl
import java.io.File

lateinit var rpcClient: RpcClientImpl

@Suppress("UNUSED")
class PaperRpcPlugin : SuspendingJavaPlugin() {

    override suspend fun onLoadAsync() {
        serverLogger = logger
        dataDir = dataFolder

        createDataDir()
        loadConfig()

        rpcClient = RpcClientImpl(fileConfig.get("host"), fileConfig.get("port"))
        IRpcClient.instance = rpcClient
    }

    override suspend fun onEnableAsync() {
        rpcClient.start()
        server.pluginManager.callEvent(RpcClientConnectEvent())
        disabled = false
    }

    override suspend fun onDisableAsync() {
        server.pluginManager.callEvent(RpcClientDisconnectEvent())
        rpcClient.close()
        disabled = true
    }

    private fun loadConfig() {
        configFile = File(dataDir, "config_client.toml")

        if (!configFile.exists()) {
            saveResource("config_client.toml", false)
        }

        loadConfig(configFile)
    }

}