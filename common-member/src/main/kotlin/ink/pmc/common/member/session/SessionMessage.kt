package ink.pmc.common.member.session

import java.util.*

data class SessionMessage(
    val type: SessionMessageType,
    val sessionType: SessionType,
    val uuid: UUID
)
