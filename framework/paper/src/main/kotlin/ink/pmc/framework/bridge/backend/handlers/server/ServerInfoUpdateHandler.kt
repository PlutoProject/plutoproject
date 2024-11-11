package ink.pmc.framework.bridge.backend.handlers.server

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification

object ServerInfoUpdateHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        if (request.serverInfoUpdate.id == internalBridge.local.id) return
        debugInfo("ServerInfoUpdateHandler: $request")
        internalBridge.syncData(request.serverInfoUpdate)
    }
}