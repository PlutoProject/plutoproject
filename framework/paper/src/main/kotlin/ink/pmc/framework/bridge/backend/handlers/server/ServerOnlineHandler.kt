package ink.pmc.framework.bridge.backend.handlers.server

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass

object ServerOnlineHandler : NotificationHandler {
    override suspend fun handle(request: BridgeRpcOuterClass.Notification) {
        TODO("Not yet implemented")
    }
}