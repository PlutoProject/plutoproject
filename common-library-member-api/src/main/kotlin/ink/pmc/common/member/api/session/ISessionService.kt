package ink.pmc.common.member.api.session

import java.util.*

interface ISessionService {

    companion object {
        lateinit var instance: ISessionService
    }

    fun isBedrockSession(uuid: UUID): Boolean

    fun isLittleSkinSession(uuid: UUID): Boolean

}