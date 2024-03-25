package ink.pmc.common.server.impl.request

import ink.pmc.common.server.message.MessageType
import ink.pmc.common.server.request.Request
import java.util.*

@Suppress("UNUSED")
class RequestImpl(
    override val sender: UUID,
    override val receivers: Set<UUID>,
    override val type: MessageType,
    override val name: String,
    override val parameters: Map<String, String>
) : Request/*, MessageImpl(sender, receivers, type, "")*/ {

    override val uniqueId: UUID = UUID.randomUUID()
    override val content: String = ""

}