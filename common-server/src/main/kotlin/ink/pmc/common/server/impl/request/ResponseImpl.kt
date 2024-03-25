package ink.pmc.common.server.impl.request

import ink.pmc.common.server.message.MessageType
import ink.pmc.common.server.request.Response
import java.util.*

@Suppress("UNUSED")
class ResponseImpl(
    override val sender: UUID,
    override val receivers: Set<UUID>,
    override val type: MessageType,
    override val target: UUID,
    override val values: Map<String, String>
) : Response/*, ReplyImpl(sender, receivers, type, "", target)*/ {

    override val uniqueId: UUID = UUID.randomUUID()
    override val content: String = ""

}