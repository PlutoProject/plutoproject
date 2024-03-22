package ink.pmc.common.server.entity

import ink.pmc.common.server.Server
import net.kyori.adventure.text.Component
import java.util.*

interface ServerEntity {

    val uniqueID: UUID
    val server: Server
    val status: EntityStatus
    val name: String
    val customName: Component
    val operator: EntityOperator<*>

}