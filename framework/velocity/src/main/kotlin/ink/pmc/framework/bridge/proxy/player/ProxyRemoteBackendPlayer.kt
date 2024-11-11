package ink.pmc.framework.bridge.proxy.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.advkt.playSound
import ink.pmc.advkt.send
import ink.pmc.advkt.showTitle
import ink.pmc.advkt.sound.SoundKt
import ink.pmc.advkt.title.ComponentTitleKt
import ink.pmc.framework.bridge.player.RemoteBackendPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.proxy.BridgeRpc
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeWorld
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.util.*

class ProxyRemoteBackendPlayer(
    val actual: Player,
    override var server: BridgeServer,
    override var world: BridgeWorld?
) : RemoteBackendPlayer() {
    override val uniqueId: UUID = actual.uniqueId
    override val name: String = actual.username

    override suspend fun sendMessage(message: String) {
        check(isOnline) { "Player offline: $name" }
        actual.sendMessage(Component.text(message))
    }

    override suspend fun sendMessage(message: Component) {
        check(isOnline) { "Player offline: $name" }
        actual.sendMessage(message)
    }

    override suspend fun sendMessage(message: RootComponentKt.() -> Unit) {
        check(isOnline) { "Player offline: $name" }
        actual.send(message)
    }

    override suspend fun showTitle(title: Title) {
        check(isOnline) { "Player offline: $name" }
        actual.showTitle(title)
    }

    override suspend fun showTitle(title: ComponentTitleKt.() -> Unit) {
        check(isOnline) { "Player offline: $name" }
        actual.showTitle(title)
    }

    override suspend fun playSound(sound: Sound) {
        check(isOnline) { "Player offline: $name" }
        actual.playSound(sound)
    }

    override suspend fun playSound(sound: SoundKt.() -> Unit) {
        check(isOnline) { "Player offline: $name" }
        actual.playSound(sound)
    }

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        return BridgeRpc.operatePlayer(request)
    }
}