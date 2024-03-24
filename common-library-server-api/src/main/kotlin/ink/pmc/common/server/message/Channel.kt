package ink.pmc.common.server.message

import ink.pmc.common.server.Server
import java.util.*

@Suppress("UNUSED")
interface Channel {

    val uniqueID: UUID
    val name: String
    val messageManager: MessageManager

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