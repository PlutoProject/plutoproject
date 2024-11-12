package ink.pmc.framework.bridge.backend.handlers.server

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification

object ServerOfflineHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        if (request.serverOffline == internalBridge.local.id) return
        if (internalBridge.getInternalRemoteServer(request.serverOffline)?.isOnline == false) return
        debugInfo("ServerOfflineHandler: $request")
        internalBridge.markRemoteServerOffline(request.serverOffline)
    }
}