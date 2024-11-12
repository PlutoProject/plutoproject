package ink.pmc.framework.bridge.backend.player

import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.backend.operationsSent
import ink.pmc.framework.bridge.checkPlayerOperationResult
import ink.pmc.framework.bridge.player.RemoteBackendPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.player.uuid
import java.util.*

open class BackendRemoteBackendPlayer(
    override val uniqueId: UUID,
    override val name: String,
    override var server: BridgeServer,
    override var world: BridgeWorld?
) : RemoteBackendPlayer() {
    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        operationsSent.add(request.id.uuid)
        val result = bridgeStub.operatePlayer(request)
        checkPlayerOperationResult(request, result)
        return result
    }
}