package ink.pmc.framework.bridge.backend.handlers.server

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification

object ServerRegistrationHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        if (request.serverRegistration.id == internalBridge.local.id) return
        debugInfo("ServerRegistrationHandler: $request")
        internalBridge.registerRemoteServer(request.serverRegistration)
    }
}