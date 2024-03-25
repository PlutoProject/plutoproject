package ink.pmc.common.server.impl.message

import ink.pmc.common.server.Server
import ink.pmc.common.server.message.Channel
import ink.pmc.common.server.message.Message
import ink.pmc.common.server.message.MessageManager
import ink.pmc.common.server.message.Reply
import ink.pmc.common.server.request.Request
import java.util.*

class ChannelImpl(
    override val messageManager: MessageManager,
    override val name: String
) : Channel {

    override val uniqueID: UUID = UUID.randomUUID()
    override val messageListeners: MutableSet<(message: Message, channel: Channel) -> Unit> = mutableSetOf()
    override val requestListener: MutableSet<(request: Request, channel: Channel) -> Unit> = mutableSetOf()

    override fun sendMessage(content: String, target: Server): Message {
        TODO("Not yet implemented")
    }

    override fun broadcast(content: String) {
        TODO("Not yet implemented")
    }

    override fun multicast(content: String, servers: Collection<Server>) {
        TODO("Not yet implemented")
    }

    override fun replyMessage(id: UUID, content: String): Reply {
        TODO("Not yet implemented")
    }

}