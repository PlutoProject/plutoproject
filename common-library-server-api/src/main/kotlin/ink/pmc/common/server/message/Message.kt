package ink.pmc.common.server.message

import java.util.*

@Suppress("UNUSED")
interface Message {

    val uniqueId: UUID
    val sender: UUID
    val receivers: Set<UUID>
    val type: MessageType
    val content: String

}