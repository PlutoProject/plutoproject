package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineStub
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification
import ink.pmc.framework.frameworkLogger
import ink.pmc.framework.rpc.RpcClient
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.data.mutableConcurrentSetOf
import ink.pmc.framework.utils.proto.empty
import io.grpc.StatusException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.*
import kotlin.time.Duration.Companion.seconds

private var monitorJob: Job? = null
internal val operationsSent = mutableConcurrentSetOf<UUID>()
val bridgeStub = BridgeRpcCoroutineStub(RpcClient.channel)

fun startBridgeMonitor() {
    monitorJob = submitAsync {
        while (true) {
            try {
                bridgeStub.monitorNotification(empty)
                    .also { frameworkLogger.info("Connecting Bridge monitor") }
                    .collect {
                        handleNotification(it)
                    }
            } catch (e: StatusException) {
                frameworkLogger.severe("Bridge monitor disconnected, wait 10s before retry")
                delay(10.seconds)
            }
        }
    }
}

fun stopBridgeMonitor() {
    monitorJob?.cancel()
    monitorJob = null
}

private suspend fun handleNotification(msg: Notification) {
    NotificationHandler[msg.contentCase].handle(msg)
}