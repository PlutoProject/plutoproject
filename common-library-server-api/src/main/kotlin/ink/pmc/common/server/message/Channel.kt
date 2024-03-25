package ink.pmc.common.server.message

import ink.pmc.common.server.Server
import ink.pmc.common.server.request.Request
import java.util.*

@Suppress("UNUSED")
interface Channel {

    val uniqueID: UUID
    val name: String
    val messageManager: MessageManager
    val messageListeners: MutableSet<(message: Message, channel: Channel) -> Unit>
    val requestListener: MutableSet<(request: Request, channel: Channel) -> Unit>

    fun onMessage(message: Message) {
        messageListeners.forEach {
            it.invoke(message, this)
        }
    }

    fun onRequest(request: Request) {
        requestListener.forEach {
            it.invoke(request, this)
        }
    }

    fun sendMessage(content: String, target: Server): Message

    fun broadcast(content: String)

    fun multicast(content: String, vararg servers: Server) {
        multicast(content, servers.toList())
    }

    fun multicast(content: String, servers: Collection<Server>)

    fun multicast(content: String, serversProvider: () -> Collection<Server>) {
        multicast(content, serversProvider.invoke())
    }

    fun replyMessage(message: Message, content: String): Reply {
        return replyMessage(message.uniqueId, content)
    }

    fun replyMessage(id: UUID, content: String): Reply

}