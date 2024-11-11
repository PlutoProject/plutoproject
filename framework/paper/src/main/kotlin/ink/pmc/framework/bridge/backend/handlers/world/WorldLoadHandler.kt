package ink.pmc.framework.bridge.backend.handlers.world

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification

object WorldLoadHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        if (request.worldLoad.server == internalBridge.local.id) return
        debugInfo("WorldLoadHandler: $request")
        internalBridge.addRemoteWorld(request.worldLoad)
    }
}