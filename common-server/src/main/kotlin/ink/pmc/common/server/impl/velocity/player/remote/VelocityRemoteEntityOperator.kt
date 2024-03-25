package ink.pmc.common.server.impl.velocity.player.remote

import ink.pmc.common.server.entity.EntityOperator
import ink.pmc.common.server.entity.ServerEntity
import ink.pmc.common.server.world.ServerLocation
import net.kyori.adventure.text.Component

open class VelocityRemoteEntityOperator : EntityOperator {

    override fun teleport(entity: ServerEntity, location: ServerLocation) {
        TODO("Not yet implemented")
    }

    override fun getCustomName(entity: ServerEntity): Component? {
        TODO("Not yet implemented")
    }

    override fun setCustomName(entity: ServerEntity, component: Component) {
        TODO("Not yet implemented")
    }

}