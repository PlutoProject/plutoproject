package ink.pmc.framework.bridge.backend.handlers.world

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass

object WorldUnloadHandler : NotificationHandler {
    override suspend fun handle(request: BridgeRpcOuterClass.Notification) {
        debugInfo("WorldUnloadHandler: $request")
        if (request.worldUnload.server == internalBridge.local.id) return
        internalBridge.removeRemoteWorld(request.worldUnload)
    }
}