package ink.pmc.framework.bridge.backend.player

import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.backend.world.BackendLocalWorld
import ink.pmc.framework.bridge.backend.world.createBridge
import ink.pmc.framework.bridge.backend.world.createBukkit
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.player.RemoteBackendPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.concurrent.sync
import ink.pmc.framework.entity.teleportSuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import java.util.*

class BackendLocalPlayer(private val actual: Player, server: BridgeServer) : RemoteBackendPlayer() {
    override val uniqueId: UUID = actual.uniqueId
    override val name: String = actual.name
    override var server: BridgeServer = server
        set(_) = error("Unsupported")
    override var world: BridgeWorld?
        get() = server.getWorld(actual.world.name) ?: BackendLocalWorld(actual.world, server)
        set(_) = error("Unsupported")
    override val location: Deferred<BridgeLocation>
        get() = CompletableDeferred(actual.location.createBridge())
    override var isOnline: Boolean
        get() = actual.isOnline
        set(_) {}

    override suspend fun teleport(location: BridgeLocation) {
        if (location.server == internalBridge.local) {
            actual.teleportSuspend(location.createBukkit())
            return
        }
        super.teleport(location)
    }

    override suspend fun sendMessage(message: Component) {
        actual.sendMessage(message)
    }

    override suspend fun showTitle(title: Title) {
        actual.showTitle(title)
    }

    override suspend fun playSound(sound: Sound) {
        actual.playSound(sound)
    }

    override suspend fun performCommand(command: String) {
        actual.sync {
            actual.performCommand(command)
        }
    }

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        return bridgeStub.operatePlayer(request)
    }
}