package ink.pmc.common.server.impl.message

import ink.pmc.common.server.message.MessageType
import ink.pmc.common.server.message.Reply
import java.util.*

open class ReplyImpl(
    override val sender: UUID,
    override val receivers: Set<UUID>,
    override val type: MessageType,
    override val content: String,
    override val target: UUID
) : Reply {

    override val uniqueId: UUID = UUID.randomUUID()

}