package ink.pmc.framework.bridge.backend.handlers.player

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass

object PlayerInfoUpdateHandler : NotificationHandler {
    override suspend fun handle(request: BridgeRpcOuterClass.Notification) {
        TODO("Not yet implemented")
    }
}