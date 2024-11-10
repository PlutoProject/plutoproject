package ink.pmc.framework.bridge.backend.player

import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.advkt.sound.SoundKt
import ink.pmc.advkt.title.ComponentTitleKt
import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld
import kotlinx.coroutines.Deferred
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.util.*

class BackendRemoteProxyPlayer(
    override val uniqueId: UUID,
    override val name: String
) : InternalPlayer() {
    override var server: BridgeServer = Bridge.master
    override val group: BridgeGroup? = null
    override val serverState: ServerState = ServerState.REMOTE
    override val serverType: ServerType = ServerType.PROXY
    override var world: BridgeWorld?
        get() = error("Unsupported")
        set(_) = error("Unsupported")
    override var isOnline: Boolean = true
        set(_) = error("Unsupported")

    override val location: Deferred<BridgeLocation>
        get() = error("Unsupported")

    override suspend fun teleport(location: BridgeLocation) {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(message: String) {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(message: Component) {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(message: RootComponentKt.() -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun showTitle(title: Title) {
        TODO("Not yet implemented")
    }

    override suspend fun showTitle(title: ComponentTitleKt.() -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun playSound(sound: Sound) {
        TODO("Not yet implemented")
    }

    override suspend fun playSound(sound: SoundKt.() -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun performCommand(command: String) {
        TODO("Not yet implemented")
    }
}