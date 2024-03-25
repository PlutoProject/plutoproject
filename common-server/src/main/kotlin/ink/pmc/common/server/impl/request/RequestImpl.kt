package ink.pmc.common.server.impl.request

import ink.pmc.common.server.UUIDSerializer
import ink.pmc.common.server.message.MessageType
import ink.pmc.common.server.request.Request
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Suppress("UNUSED")
class RequestImpl(
    @Serializable(with = UUIDSerializer::class)
    override val sender: UUID,
    override val receivers: Set<@Serializable(with = UUIDSerializer::class) UUID>,
    override val type: MessageType,
    override val name: String,
    override val parameters: Map<String, String>
) : Request {

    @Serializable(with = UUIDSerializer::class)
    override val uniqueId: UUID = UUID.randomUUID()
    override val content: String = ""

}