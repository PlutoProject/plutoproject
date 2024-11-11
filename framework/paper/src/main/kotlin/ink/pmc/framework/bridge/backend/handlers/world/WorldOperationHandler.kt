package ink.pmc.framework.bridge.backend.handlers.world

import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.backend.operationsSent
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass
import ink.pmc.framework.utils.player.uuid

object WorldOperationHandler : NotificationHandler {
    override suspend fun handle(request: BridgeRpcOuterClass.Notification) {
        debugInfo("WorldOperationHandler: $request")
        if (operationsSent.remove(request.worldOperation.id.uuid)) return
    }
}