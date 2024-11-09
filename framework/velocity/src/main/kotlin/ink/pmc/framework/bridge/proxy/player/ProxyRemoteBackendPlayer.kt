package ink.pmc.framework.bridge.proxy.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.framework.bridge.player.RemoteBackendPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.proxy.BridgeRpc
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeWorld

class ProxyRemoteBackendPlayer(
    val actual: Player,
    server: BridgeServer,
    world: BridgeWorld? = null,
) : RemoteBackendPlayer(actual.uniqueId, actual.username, server, world) {
    override var isOnline: Boolean
        get() = actual.isActive
        set(_) = error("Unsupported")

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        return BridgeRpc.operatePlayer(request)
    }
}