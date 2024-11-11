package ink.pmc.framework.bridge.backend.handlers.player

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification

object PlayerJoinHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        debugInfo("PlayerJoinHandler: $request")
        if (request.playerJoin.server == internalBridge.local.id) return
        internalBridge.addRemotePlayer(request.playerJoin)
    }
}