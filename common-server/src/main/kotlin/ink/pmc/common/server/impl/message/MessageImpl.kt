package ink.pmc.common.server.impl.message

import ink.pmc.common.server.message.Message
import ink.pmc.common.server.message.MessageType
import java.util.*

@Suppress("UNUSED")
open class MessageImpl(
    override val sender: UUID,
    override val receivers: Set<UUID>,
    override val channel: UUID,
    override val type: MessageType,
    override val content: String
) : Message {

    override val uniqueId: UUID = UUID.randomUUID()

}