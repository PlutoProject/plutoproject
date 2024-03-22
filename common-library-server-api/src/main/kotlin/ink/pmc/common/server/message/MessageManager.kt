package ink.pmc.common.server.message

import java.util.*

interface MessageManager {

    fun getReplies(message: Message): Set<ReplyMessage>

    fun getReplies(message: UUID): Set<ReplyMessage>

}