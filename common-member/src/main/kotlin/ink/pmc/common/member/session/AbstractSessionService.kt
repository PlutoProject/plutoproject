package ink.pmc.common.member.session

import ink.pmc.common.member.api.session.ISessionService
import java.util.*

const val CHANNEL_NAMESPACE = "member"
const val CHANNEL_NAME = "session_message"
const val FULL_CHANNEL_NAME = "$CHANNEL_NAMESPACE:$CHANNEL_NAME"

abstract class AbstractSessionService : ISessionService {

    val bedrockSessions = mutableSetOf<UUID>()
    val littleSkinSession = mutableSetOf<UUID>()

    override fun isBedrockSession(uuid: UUID): Boolean {
        return bedrockSessions.contains(uuid)
    }

    override fun isLittleSkinSession(uuid: UUID): Boolean {
        return littleSkinSession.contains(uuid)
    }

    abstract fun messageAdd(type: SessionType, uuid: UUID)

    abstract fun messageRemove(type: SessionType, uuid: UUID)

    abstract fun init()

}