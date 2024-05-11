package ink.pmc.common.member.api

import ink.pmc.common.member.api.data.DataContainer
import ink.pmc.common.member.api.data.MemberModifier
import ink.pmc.common.member.api.punishment.Punishment
import java.time.Instant
import java.util.*
import javax.xml.stream.events.Comment

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

    suspend fun lookup(name: String, authType: AuthType = AuthType.OFFICIAL): Member?

    suspend fun exist(uid: Long): Boolean

    suspend fun exist(uuid: UUID): Boolean

    suspend fun exist(name: String, authType: AuthType = AuthType.OFFICIAL): Boolean

    suspend fun existPunishment(id: Long): Boolean

    suspend fun existComment(id: Long): Boolean

    suspend fun existDataContainer(id: Long): Boolean

    suspend fun existBedrockAccount(id: Long): Boolean

    suspend fun lookupPunishment(id: Long): Punishment?

    suspend fun lookupComment(id: Long): Comment?

    suspend fun lookupDataContainer(id: Long): DataContainer?

    suspend fun lookupBedrockAccount(id: Long): BedrockAccount?

    suspend fun isWhitelisted(uid: Long): Boolean

    suspend fun isWhitelisted(uuid: UUID): Boolean

    suspend fun modifier(uid: Long, refresh: Boolean = false): MemberModifier?

    suspend fun modifier(uuid: UUID, refresh: Boolean = false): MemberModifier?

    suspend fun save(member: Member)

    suspend fun reload(member: Member): Member?

    suspend fun save(uid: Long)

    suspend fun reload(uid: Long): Member?

    suspend fun save(uuid: UUID)

    suspend fun reload(uuid: UUID): Member?

}