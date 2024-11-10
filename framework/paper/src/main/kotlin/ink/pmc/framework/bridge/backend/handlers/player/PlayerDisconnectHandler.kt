package ink.pmc.framework.bridge.backend.handlers.player

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification

object PlayerDisconnectHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        TODO("Not yet implemented")
    }
}