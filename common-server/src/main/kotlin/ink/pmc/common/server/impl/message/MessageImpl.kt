package ink.pmc.common.server.impl.message

import ink.pmc.common.server.UUIDSerializer
import ink.pmc.common.server.message.Message
import ink.pmc.common.server.message.MessageType
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Suppress("UNUSED")
class MessageImpl(
    @Serializable(with = UUIDSerializer::class)
    override val sender: UUID,
    override val receivers: Set<@Serializable(with = UUIDSerializer::class) UUID>,
    override val type: MessageType,
    override val content: String
) : Message {

    @Serializable(with = UUIDSerializer::class)
    override val uniqueId: UUID = UUID.randomUUID()

}