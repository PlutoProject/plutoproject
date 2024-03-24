package ink.pmc.common.server.entity

import ink.pmc.common.server.world.ServerLocation

interface EntityOperator<T> {

    fun teleport(entity: ServerEntity, location: ServerLocation)

}