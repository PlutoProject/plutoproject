package ink.pmc.common.server.impl.velocity.player.local

import ink.pmc.common.server.entity.EntityOperator
import ink.pmc.common.server.entity.ServerEntity
import ink.pmc.common.server.world.ServerLocation
import net.kyori.adventure.text.Component

open class VelocityLocalEntityOperator : EntityOperator {

    val unsupported = "Operation not allowed on Velocity."

    override fun teleport(entity: ServerEntity, location: ServerLocation) {
        throw UnsupportedOperationException(unsupported)
    }

    override fun getCustomName(entity: ServerEntity): Component? {
        throw UnsupportedOperationException(unsupported)
    }

    override fun setCustomName(entity: ServerEntity, component: Component) {
        throw UnsupportedOperationException(unsupported)
    }

}