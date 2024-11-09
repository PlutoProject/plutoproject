package ink.pmc.framework.bridge.backend.player

import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.backend.operationsSent
import ink.pmc.framework.bridge.player.RemoteBackendPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.player.uuid
import java.util.*

class BackendRemoteBackendPlayer(
    uniqueId: UUID,
    name: String,
    server: BridgeServer,
    world: BridgeWorld?
) : RemoteBackendPlayer(uniqueId, name, server, world) {
    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        operationsSent.add(request.id.uuid)
        return bridgeStub.operatePlayer(request)
    }
}