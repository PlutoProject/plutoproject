package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.player.createInfoWithoutLocation
import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineStub
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.ServerInfo
import ink.pmc.framework.bridge.proto.heartbeatMessage
import ink.pmc.framework.bridge.proto.serverInfo
import ink.pmc.framework.bridge.world.createInfo
import ink.pmc.framework.frameworkLogger
import ink.pmc.framework.rpc.RpcClient
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.data.getValue
import ink.pmc.framework.utils.data.mutableConcurrentSetOf
import ink.pmc.framework.utils.data.setValue
import ink.pmc.framework.utils.proto.empty
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import java.util.logging.Level
import kotlin.time.Duration.Companion.seconds

private var isInitialized by MutableStateFlow(false)
private var isConnected by MutableStateFlow(false)
private var monitorJob: Job? = null
private var heartbeatJob: Job? = null
internal val operationsSent = mutableConcurrentSetOf<UUID>()
val bridgeStub = BridgeRpcCoroutineStub(RpcClient.channel)

private fun getCurrentServerInfo(): ServerInfo {
    return serverInfo {
        val localServer = internalBridge.local
        id = localServer.id
        localServer.group?.let { group = it.id }
        backend = true
        players.addAll(localServer.players.map { it.createInfoWithoutLocation() })
        worlds.addAll(localServer.worlds.map { it.createInfo() })
    }
}

private suspend fun registerServer() {
    debugInfo("Trying to register server")
    val result = bridgeStub.registerServer(getCurrentServerInfo())
    result.serversList.forEach {
        if (it.id == internalBridge.local.id) return@forEach
        internalBridge.registerRemoteServer(it)
    }
    isInitialized = true
    debugInfo("Server registered")
}

private suspend fun monitor() = runCatching {
    bridgeStub.monitorNotification(empty).also {
        debugInfo("Trying to monitor")
        // 已初始化但从 Master 断开：可能是因为网络问题，同步一次数据防止不一致
        if (isInitialized && !isConnected) {
            debugInfo("Trying to sync data")
            val result = bridgeStub.syncData(getCurrentServerInfo())
            if (result.notRegistered) {
                registerServer()
            }
            debugInfo("Data synced")
        }
        // 未初始化：注册服务器
        if (!isInitialized) {
            registerServer()
        }
        isConnected = true
        frameworkLogger.info("Bridge monitor connected successfully")
        debugInfo("Monitor connected")
    }.collect {
        handleNotification(it)
    }
}.onFailure {
    if (isConnected) {
        frameworkLogger.log(Level.SEVERE, "Bridge monitor disconnected, wait 5s before retry", it)
    } else {
        frameworkLogger.log(Level.SEVERE, "Failed to connect Bridge monitor, wait 5s before retry", it)
    }
    isConnected = false
    delay(5.seconds)
}

private fun startMonitor() {
    monitorJob = submitAsync {
        while (true) {
            monitor()
        }
    }
}

private suspend fun heartbeat() = runCatching {
    val result = bridgeStub.heartbeat(heartbeatMessage {
        server = internalBridge.local.id
    })
    // Master 报告未注册：可能是因为 Master 重启了，重新初始化
    if (result.notRegistered && isInitialized) {
        debugInfo("Master responded notRegistered")
        isInitialized = false
        isConnected = false
    }
}

private fun startHeartbeat() {
    heartbeatJob = submitAsync {
        while (true) {
            delay(5.seconds)
            heartbeat()
        }
    }
}

fun startBridgeBackgroundTask() {
    startMonitor()
    startHeartbeat()
}

fun stopBridgeBackgroundTask() {
    monitorJob?.cancel()
    heartbeatJob?.cancel()
    monitorJob = null
    heartbeatJob = null
}

private suspend fun handleNotification(msg: Notification) {
    NotificationHandler[msg.contentCase].handle(msg)
}