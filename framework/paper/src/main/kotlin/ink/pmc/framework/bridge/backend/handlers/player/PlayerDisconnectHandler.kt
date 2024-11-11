package ink.pmc.framework.bridge.backend.handlers.player

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification
import ink.pmc.framework.utils.player.uuid

object PlayerDisconnectHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        debugInfo("PlayerDisconnectHandler: $request")
        if (request.playerDisconnect.server == internalBridge.local.id) return
        internalBridge.removeRemotePlayers(request.playerDisconnect.uniqueId.uuid)
    }
}