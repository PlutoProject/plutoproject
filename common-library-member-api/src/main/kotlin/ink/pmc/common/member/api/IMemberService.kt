package ink.pmc.common.member.api

import java.time.LocalDateTime

@Suppress("UNUSED", "INAPPLICABLE_JVM_NAME")
interface IMemberService {

    companion object {
        lateinit var instance: IMemberService
    }

    suspend fun lastUid(): Long

    suspend fun lastMember(): Member?

    suspend fun lastMemberCreatedAt(): LocalDateTime?

    suspend fun create(name: String, authType: AuthType = AuthType.OFFICIAL): Member?

    suspend fun lookup(uid: Long): Member?

    operator fun get(uid: Long): Member?

    suspend fun exist(uid: Long): Boolean

    suspend fun nonExist(uid: Long): Boolean

    suspend fun update(member: Member)

    suspend fun refresh(member: Member)

}