package ink.pmc.common.member.api

import java.time.Instant
import java.util.*

@Suppress("UNUSED", "INAPPLICABLE_JVM_NAME")
interface IMemberService {

    companion object {
        lateinit var instance: IMemberService
    }

    suspend fun lastUid(): Long

    suspend fun lastMember(): Member?

    suspend fun lastMemberCreatedAt(): Instant?

    suspend fun create(name: String, authType: AuthType = AuthType.OFFICIAL): Member?

    suspend fun lookup(uid: Long): Member?

    suspend fun lookup(uuid: UUID): Member?

    operator fun get(uid: Long): Member?

    suspend fun exist(uid: Long): Boolean

    suspend fun exist(uuid: UUID): Boolean

    suspend fun existPunishment(id: Long): Boolean

    suspend fun existComment(id: Long): Boolean

    suspend fun existDataContainer(id: Long): Boolean

    suspend fun existBedrockAccount(id: Long): Boolean

    suspend fun update(member: Member)

    suspend fun refresh(member: Member): Member?

}