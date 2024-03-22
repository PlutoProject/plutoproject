package ink.pmc.common.server.player

import ink.pmc.common.server.Server
import ink.pmc.common.server.entity.EntityStatus
import ink.pmc.common.server.entity.ServerEntity
import ink.pmc.common.server.world.ServerLocation
import ink.pmc.common.server.world.ServerWorld
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title

@Suppress("UNUSED")
interface ServerPlayer : ServerEntity {

    val displayName: Component
    override val operator: PlayerOperator<*>
    val isOnline: Boolean
        get() = status != EntityStatus.OFFLINE

    fun sendMessage(component: Component)

    fun sendActionbar(component: Component)

    fun sendTitle(title: Title)

    fun playSound(sound: Sound)

    fun switchServer(target: Server)

    fun teleport(location: ServerLocation)

    fun teleport(world: ServerWorld, x: Double, y: Double, z: Double)

}