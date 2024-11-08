package ink.pmc.framework.bridge.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.advkt.sound.SoundKt
import ink.pmc.advkt.title.TitleKt
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerElement
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld
import kotlinx.coroutines.Deferred
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.util.*

class ProxyRemoteBackendPlayer(
    private val actual: Player,
    override val server: BridgeServer,
    override val world: BridgeWorld
) : BridgePlayer {
    override val group: BridgeGroup?
        get() = TODO("Not yet implemented")
    override val serverType: ServerType
        get() = TODO("Not yet implemented")
    override val uniqueId: UUID
        get() = TODO("Not yet implemented")
    override val name: String
        get() = TODO("Not yet implemented")

    override val location: Deferred<BridgeLocation>
        get() = TODO("Not yet implemented")

    override val isOnline: Boolean
        get() = TODO("Not yet implemented")

    override suspend fun teleport(location: BridgeLocation) {
        TODO("Not yet implemented")
    }

    override suspend fun teleport(world: BridgeWorld) {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(message: String) {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(message: Component) {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(component: RootComponentKt.() -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun showTitle(title: Title) {
        TODO("Not yet implemented")
    }

    override suspend fun showTitle(title: TitleKt.() -> Unit) {
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

    override fun <T : ServerElement> convertElement(type: ServerType): T? {
        TODO("Not yet implemented")
    }
}