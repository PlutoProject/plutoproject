package ink.pmc.framework.bridge.backend.player

import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.player.RemoteBackendPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeWorld
import org.bukkit.entity.Player

class BackendRemoteBackendPlayer(
    actual: Player,
    server: BridgeServer,
    world: BridgeWorld?
) : RemoteBackendPlayer(actual.uniqueId, actual.name, server, world) {
    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        return bridgeStub.operatePlayer(request)
    }
}