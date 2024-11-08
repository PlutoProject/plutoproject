package ink.pmc.framework.bridge.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.advkt.sound.SoundKt
import ink.pmc.advkt.title.ComponentTitleKt
import ink.pmc.framework.bridge.InternalPlayer
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.server.localServer
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld
import kotlinx.coroutines.Deferred
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.util.*

class ProxyRemoteBackendPlayer(
    actual: Player,
    override val server: BridgeServer,
    override val world: BridgeWorld
) : InternalPlayer() {
    override val group: BridgeGroup? = server.group
    override val serverType: ServerType = ServerType.REMOTE_BACKEND
    override val uniqueId: UUID = actual.uniqueId
    override val name: String = actual.username
    override val location: Deferred<BridgeLocation>
        get() = TODO("Not yet implemented")
    override var isOnline: Boolean = true
    private val local = localServer.getPlayer(uniqueId)!!

    override suspend fun teleport(location: BridgeLocation) {
        TODO("Not yet implemented")
    }

    override suspend fun teleport(world: BridgeWorld) {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(message: String) {
        local.sendMessage(message)
    }

    override suspend fun sendMessage(message: Component) {
        local.sendMessage(message)
    }

    override suspend fun sendMessage(message: RootComponentKt.() -> Unit) {
        local.sendMessage(message)
    }

    override suspend fun showTitle(title: Title) {
        local.showTitle(title)
    }

    override suspend fun showTitle(title: ComponentTitleKt.() -> Unit) {
        local.showTitle(title)
    }

    override suspend fun playSound(sound: Sound) {
        local.playSound(sound)
    }

    override suspend fun playSound(sound: SoundKt.() -> Unit) {
        local.playSound(sound)
    }

    override suspend fun performCommand(command: String) {
        TODO("Not yet implemented")
    }

    override fun convertElement(type: ServerType): BridgePlayer? {
        if (type == serverType) return this
        if (type == ServerType.REMOTE_PROXY) return null
        return super.convertElement(type)
    }
}