package ink.pmc.common.refactor.member.api

import ink.pmc.common.refactor.member.api.dsl.PardonDsl
import ink.pmc.common.refactor.member.api.dsl.PunishmentDsl
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

    fun create(name: String, authType: AuthType = AuthType.OFFICIAL): Member?

    fun create(name: String, uuid: UUID, authType: AuthType = AuthType.OFFICIAL): Member?

    operator fun get(uid: Long): Member?

    operator fun get(name: String, authType: AuthType = AuthType.OFFICIAL): Member?

    operator fun get(uuid: UUID, authType: AuthType = AuthType.OFFICIAL): Member?

    fun exist(uid: Long)

    fun exist(name: String, authType: AuthType = AuthType.OFFICIAL)

    fun exist(uuid: UUID, authType: AuthType = AuthType.OFFICIAL)

    fun nonExist(uid: Long)

    fun nonExist(name: String, authType: AuthType = AuthType.OFFICIAL)

    fun nonExist(uuid: UUID, authType: AuthType = AuthType.OFFICIAL)

    fun punish(block: PunishmentDsl.() -> Unit)

    fun hasPunishment(id: Long): Boolean

    fun pardon(block: PardonDsl.() -> Unit)

    fun update(member: Member)

    fun refresh(member: Member)

}