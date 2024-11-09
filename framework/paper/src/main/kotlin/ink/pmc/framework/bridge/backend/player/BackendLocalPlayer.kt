package ink.pmc.framework.bridge.backend.player

import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.advkt.playSound
import ink.pmc.advkt.send
import ink.pmc.advkt.showTitle
import ink.pmc.advkt.sound.SoundKt
import ink.pmc.advkt.title.ComponentTitleKt
import ink.pmc.framework.bridge.backend.backendBridge
import ink.pmc.framework.bridge.backend.world.BackendLocalWorld
import ink.pmc.framework.bridge.backend.world.createBridge
import ink.pmc.framework.bridge.backend.world.createBukkit
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.entity.teleportSuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import java.util.*

class BackendLocalPlayer(private val actual: Player) : InternalPlayer() {
    override val uniqueId: UUID = actual.uniqueId
    override val name: String = actual.name
    override var server: BridgeServer = backendBridge.local
        set(_) = error("Unsupported")
    override val group: BridgeGroup? = server.group
    override val serverState: ServerState = ServerState.LOCAL
    override val serverType: ServerType = ServerType.BACKEND
    override var world: BridgeWorld?
        get() = BackendLocalWorld(actual.world)
        set(_) = error("Unsupported")
    override val location: Deferred<BridgeLocation>
        get() = CompletableDeferred(actual.location.createBridge())
    override var isOnline: Boolean
        get() = actual.isOnline
        set(_) = error("Unsupported")

    override suspend fun teleport(location: BridgeLocation) {
        actual.teleportSuspend(location.createBukkit())
    }

    override suspend fun sendMessage(message: String) {
        actual.sendMessage(message)
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
        actual.performCommand(command)
    }
}