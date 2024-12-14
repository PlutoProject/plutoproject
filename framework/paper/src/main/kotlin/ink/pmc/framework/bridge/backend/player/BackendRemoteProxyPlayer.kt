package ink.pmc.framework.bridge.backend.player

import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.checkPlayerOperationResult
import ink.pmc.framework.bridge.player.RemotePlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.warn
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld
import kotlinx.coroutines.Deferred
import net.kyori.adventure.sound.Sound
import java.util.*

class BackendRemoteProxyPlayer(
    override val uniqueId: UUID,
    override val name: String,
    override var server: BridgeServer
) : RemotePlayer() {
    override var world: BridgeWorld?
        get() = convertElement(ServerState.REMOTE, ServerType.BACKEND)?.world ?: error("Unsupported")
        set(_) = error("Unsupported")
    override val location: Deferred<BridgeLocation>
        get() = convertElement(ServerState.REMOTE, ServerType.BACKEND)?.location ?: error("Unsupported")

    override suspend fun teleport(location: BridgeLocation) {
        convertElement(ServerState.REMOTE, ServerType.BACKEND)?.teleport(location)
            ?: warn { error("Unsupported") }
    }

    override suspend fun playSound(sound: Sound) {
        convertElement(ServerState.REMOTE, ServerType.BACKEND)?.playSound(sound)
            ?: warn { error("Unsupported") }
    }

    override suspend fun performCommand(command: String) {
        convertElement(ServerState.REMOTE, ServerType.BACKEND)?.performCommand(command)
            ?: warn { error("Unsupported") }
    }

    override suspend fun switchServer(server: String) {
        convertElement(ServerState.REMOTE, ServerType.BACKEND)?.switchServer(server)
            ?: warn { error("Unsupported") }
    }

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        val result = bridgeStub.operatePlayer(request)
        checkPlayerOperationResult(request, result)
        return result
    }
}