package ink.pmc.framework.bridge.backend.handlers.world

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification

object WorldInfoUpdateHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        if (request.worldInfoUpdate.server == internalBridge.local.id) return
        debugInfo("WorldInfoUpdateHandler: $request")
        internalBridge.updateRemoteWorldInfo(request.worldInfoUpdate)
    }
}