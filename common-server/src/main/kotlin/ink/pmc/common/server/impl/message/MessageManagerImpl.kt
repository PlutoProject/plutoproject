package ink.pmc.common.server.impl.message

import ink.pmc.common.server.message.Message
import ink.pmc.common.server.message.MessageManager
import ink.pmc.common.server.message.Reply
import ink.pmc.common.server.request.Request
import ink.pmc.common.server.request.Response
import java.util.*

class MessageManagerImpl : MessageManager {

    override val inboundQueue: Queue<Message> = LinkedList()
    override val outboundQueue: Queue<Message> = LinkedList()

    override fun getReplies(message: Message): Set<Reply> {
        TODO("Not yet implemented")
    }

    override fun getReplies(message: UUID): Set<Reply> {
        TODO("Not yet implemented")
    }

    override fun getResponse(request: Request): Set<Response> {
        TODO("Not yet implemented")
    }

    override fun getResponse(request: UUID): Set<Response> {
        TODO("Not yet implemented")
    }

}