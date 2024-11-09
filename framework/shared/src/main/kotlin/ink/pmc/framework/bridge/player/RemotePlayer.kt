package ink.pmc.framework.bridge.player

import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.advkt.sound.SoundKt
import ink.pmc.advkt.title.ComponentTitleKt
import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeWorld
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.util.*

abstract class RemotePlayer(
    final override val uniqueId: UUID,
    final override val name: String,
    final override var server: BridgeServer,
    override var world: BridgeWorld? = null
) : InternalPlayer() {
    private val local = Bridge.local.getPlayer(uniqueId)
        ?: error("Corresponding local player instance not found: $name")
    override val group: BridgeGroup? = server.group
    override val serverType: ServerType = ServerType.BACKEND
    override val serverState: ServerState = ServerState.REMOTE
    override var isOnline: Boolean = true

    override suspend fun sendMessage(message: String) {
        check(isOnline) { "Player offline: $name" }
        local.sendMessage(message)
    }

    override suspend fun sendMessage(message: Component) {
        check(isOnline) { "Player offline: $name" }
        local.sendMessage(message)
    }

    override suspend fun sendMessage(message: RootComponentKt.() -> Unit) {
        check(isOnline) { "Player offline: $name" }
        local.sendMessage(message)
    }

    override suspend fun showTitle(title: Title) {
        check(isOnline) { "Player offline: $name" }
        local.showTitle(title)
    }

    override suspend fun showTitle(title: ComponentTitleKt.() -> Unit) {
        check(isOnline) { "Player offline: $name" }
        local.showTitle(title)
    }

    override suspend fun playSound(sound: Sound) {
        check(isOnline) { "Player offline: $name" }
        local.playSound(sound)
    }

    override suspend fun playSound(sound: SoundKt.() -> Unit) {
        check(isOnline) { "Player offline: $name" }
        local.playSound(sound)
    }
}