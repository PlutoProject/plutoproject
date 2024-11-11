package ink.pmc.framework.bridge.proxy.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.advkt.playSound
import ink.pmc.advkt.send
import ink.pmc.advkt.showTitle
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

class ProxyLocalPlayer(private val actual: Player, server: BridgeServer) : InternalPlayer() {
    override var server: BridgeServer = server
        set(_) = error("Unsupported")
    override val group: BridgeGroup? = Bridge.local.group
    override val serverType: ServerType = Bridge.local.type
    override val serverState: ServerState = Bridge.local.state
    override val uniqueId: UUID = actual.uniqueId
    override val name: String = actual.username
    override val location: Deferred<BridgeLocation>
        get() = error("Unsupported")
    override var world: BridgeWorld?
        set(_) = error("Unsupported")
        get() = error("Unsupported")
    override var isOnline: Boolean
        set(_) = error("Unsupported")
        get() = actual.isActive


    override suspend fun teleport(location: BridgeLocation) {
        error("Unsupported")
    }

    override suspend fun sendMessage(message: String) {
        actual.sendMessage(Component.text(message))
    }

    override suspend fun sendMessage(message: Component) {
        actual.sendMessage(message)
    }

    override suspend fun sendMessage(message: RootComponentKt.() -> Unit) {
        actual.send(message)
    }

    override suspend fun showTitle(title: Title) {
        actual.showTitle(title)
    }

    override suspend fun showTitle(title: ComponentTitleKt.() -> Unit) {
        actual.showTitle(title)
    }

    override suspend fun playSound(sound: Sound) {
        actual.playSound(sound)
    }

    override suspend fun playSound(sound: SoundKt.() -> Unit) {
        actual.playSound(sound)
    }

    override suspend fun performCommand(command: String) {
        error("Unsupported")
    }
}