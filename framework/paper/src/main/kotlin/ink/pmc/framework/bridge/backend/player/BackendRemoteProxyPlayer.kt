package ink.pmc.framework.bridge.backend.player

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.player.RemotePlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld
import kotlinx.coroutines.Deferred
import java.util.*

class BackendRemoteProxyPlayer(
    override val uniqueId: UUID,
    override val name: String
) : RemotePlayer() {
    override var server: BridgeServer = Bridge.master
    override var world: BridgeWorld?
        get() = error("Unsupported")
        set(_) = error("Unsupported")
    override val location: Deferred<BridgeLocation>
        get() = error("Unsupported")

    override suspend fun teleport(location: BridgeLocation) {
        error("Unsupported")
    }

    override suspend fun performCommand(command: String) {
        error("Unsupported")
    }

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        return bridgeStub.operatePlayer(request)
    }
}