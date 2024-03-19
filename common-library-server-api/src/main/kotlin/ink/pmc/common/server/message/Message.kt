package ink.pmc.common.server.message

import ink.pmc.common.server.Server
import java.util.*

@Suppress("UNUSED")
interface Message {

    val uniqueId: UUID
    val sender: Server
    val receivers: Set<Server>
    val type: MessageType
    val content: String

}