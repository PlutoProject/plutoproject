package ink.pmc.framework.bridge.backend.handlers.player

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification

object PlayerSwitchServerHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        if (request.playerSwitchServer.server == internalBridge.local.id) return
        debugInfo("PlayerSwitchServerHandler: $request")
        internalBridge.remotePlayerSwitchServer(request.playerSwitchServer)
    }
}