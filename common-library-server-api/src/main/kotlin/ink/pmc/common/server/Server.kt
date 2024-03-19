package ink.pmc.common.server

import ink.pmc.common.server.message.MessageManager
import ink.pmc.common.server.network.Network
import org.apache.logging.log4j.message.Message
import java.util.*

@Suppress("UNUSED")
interface Server {

    val uniqueId: UUID
    val name: String
    val network: Network?
    val onlinePlayerCount: Int
    val messageManager: MessageManager

    val isInNetwork: Boolean
        get() = network != null

    fun sendMessage(content: String): Message

    fun replyMessage(content: String, messageToReply: UUID): Message

    fun replyMessage(content: String, messageToReply: Message): Message

}