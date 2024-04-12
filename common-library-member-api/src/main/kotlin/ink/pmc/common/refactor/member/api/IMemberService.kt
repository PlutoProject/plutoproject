package ink.pmc.common.refactor.member.api

import java.time.LocalDateTime
import java.util.*

@Suppress("UNUSED", "INAPPLICABLE_JVM_NAME")
interface IMemberService {

    companion object {
        lateinit var instance: IMemberService
    }

    val lastUid: Long?
    val lastMember: Member?
    val lastMemberCreatedAt: LocalDateTime?

    suspend fun create(name: String, authType: AuthType = AuthType.OFFICIAL): Member?

    suspend fun create(name: String, uuid: UUID, authType: AuthType = AuthType.OFFICIAL): Member?

    suspend fun lookup(uid: Long): Member?

    operator fun get(uid: Long): Member?

    suspend fun exist(uid: Long)

    suspend fun nonExist(uid: Long)

    suspend fun update(uid: Long)

    suspend fun refresh(uid: Long)

}