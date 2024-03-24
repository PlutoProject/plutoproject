package ink.pmc.common.server.message

import java.util.*

interface Reply : Message {

    val replying: UUID

}