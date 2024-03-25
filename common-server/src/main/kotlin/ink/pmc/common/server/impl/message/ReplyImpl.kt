package ink.pmc.common.server.impl.message

import ink.pmc.common.server.UUIDSerializer
import ink.pmc.common.server.message.MessageType
import ink.pmc.common.server.message.Reply
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class ReplyImpl(
    @Serializable(with = UUIDSerializer::class)
    override val sender: UUID,
    override val receivers: Set<@Serializable(with = UUIDSerializer::class) UUID>,
    override val type: MessageType,
    override val content: String,
    @Serializable(with = UUIDSerializer::class)
    override val target: UUID
) : Reply {

    @Serializable(with = UUIDSerializer::class)
    override val uniqueId: UUID = UUID.randomUUID()

}