package ink.pmc.common.server.entity

import ink.pmc.common.server.world.ServerLocation
import net.kyori.adventure.text.Component

interface EntityOperator {

    fun teleport(entity: ServerEntity, location: ServerLocation)

    fun getCustomName(entity: ServerEntity): Component?

    fun setCustomName(entity: ServerEntity, component: Component)

}