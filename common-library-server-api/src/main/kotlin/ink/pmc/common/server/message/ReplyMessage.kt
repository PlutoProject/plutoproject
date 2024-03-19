package ink.pmc.common.server.message

import java.util.UUID

interface ReplyMessage : Message {

    val replying: UUID

}