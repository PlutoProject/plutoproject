package ink.pmc.framework.bridge.backend.handlers.server

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification

object ServerOnlineHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        debugInfo("ServerOnlineHandler: $request")
        if (request.serverOnline == internalBridge.local.id) return
        internalBridge.markRemoteServerOnline(request.serverOnline)
    }
}