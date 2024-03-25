package ink.pmc.common.server.impl.player

import ink.pmc.common.server.Server
import ink.pmc.common.server.entity.EntityStatus
import ink.pmc.common.server.player.PlayerOperator
import ink.pmc.common.server.player.ServerPlayer
import ink.pmc.common.server.world.ServerWorld
import java.util.*

@Suppress("UNUSED")
class ServerPlayerImpl(
    override val uniqueID: UUID,
    override val server: Server,
    override val status: EntityStatus,
    override val name: String,
    override val operator: PlayerOperator
) : ServerPlayer {

    override fun teleport(world: ServerWorld, x: Double, y: Double, z: Double) {
        TODO("Not yet implemented")
    }

}