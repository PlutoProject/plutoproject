package ink.pmc.framework.bridge.proxy.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.warn
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.player.switchServer
import kotlinx.coroutines.Deferred
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.util.*

class ProxyLocalPlayer(private val actual: Player, server: BridgeServer) : InternalPlayer() {
    override var server: BridgeServer = server
        set(_) = error("Unsupported")
    override val group: BridgeGroup? = Bridge.local.group
    override val serverType: ServerType = Bridge.local.type
    override val serverState: ServerState = Bridge.local.state
    override val uniqueId: UUID = actual.uniqueId
    override val name: String = actual.username
    override val location: Deferred<BridgeLocation>
        get() = convertElement(ServerState.REMOTE, ServerType.BACKEND)?.location ?: error("Unsupported")
    override var world: BridgeWorld?
        get() = convertElement(ServerState.REMOTE, ServerType.BACKEND)?.world ?: error("Unsupported")
        set(_) = error("Unsupported")
    override var isOnline: Boolean
        get() = actual.isActive
        set(_) {}

    override suspend fun teleport(location: BridgeLocation) {
        convertElement(ServerState.REMOTE, ServerType.BACKEND)?.teleport(location)
            ?: warn { error("Unsupported") }
    }

    override suspend fun sendMessage(message: Component) {
        actual.sendMessage(message)
    }

    override suspend fun showTitle(title: Title) {
        actual.showTitle(title)
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
        actual.switchServer(server)
    }
}