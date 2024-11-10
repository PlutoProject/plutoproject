package ink.pmc.framework.bridge.backend.handlers.server

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification

object ServerRegistrationHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        TODO("Not yet implemented")
    }
}