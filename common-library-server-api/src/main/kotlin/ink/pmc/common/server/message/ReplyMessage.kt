package ink.pmc.common.server.message

import java.util.*

interface ReplyMessage : Message {

    val replying: UUID

}