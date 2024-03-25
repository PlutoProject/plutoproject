package ink.pmc.common.server.impl.request

import ink.pmc.common.server.UUIDSerializer
import ink.pmc.common.server.message.MessageType
import ink.pmc.common.server.request.Response
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Suppress("UNUSED")
class ResponseImpl(
    @Serializable(with = UUIDSerializer::class)
    override val sender: UUID,
    override val receivers: Set<@Serializable(with = UUIDSerializer::class) UUID>,
    override val type: MessageType,
    @Serializable(with = UUIDSerializer::class)
    override val target: UUID,
    override val values: Map<String, String>
) : Response {

    @Serializable(with = UUIDSerializer::class)
    override val uniqueId: UUID = UUID.randomUUID()
    override val content: String = ""

}