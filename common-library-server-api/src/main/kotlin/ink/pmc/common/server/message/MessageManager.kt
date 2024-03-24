package ink.pmc.common.server.message

import ink.pmc.common.server.request.Request
import ink.pmc.common.server.request.Response
import java.util.*

interface MessageManager {

    val inboundQueue: Queue<Message>
    val outboundQueue: Queue<Message>

    fun getReplies(message: Message): Set<Reply>

    fun getReplies(message: UUID): Set<Reply>

    fun getResponse(request: Request): Set<Response>

    fun getResponse(request: UUID): Set<Response>

}