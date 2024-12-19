package ink.pmc.framework.bridge.backend.handlers.player

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification
import ink.pmc.framework.player.uuid

object PlayerDisconnectHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        debugInfo("PlayerDisconnectHandler: $request")
        internalBridge.removeRemotePlayers(request.playerDisconnect.uniqueId.uuid)
    }
}