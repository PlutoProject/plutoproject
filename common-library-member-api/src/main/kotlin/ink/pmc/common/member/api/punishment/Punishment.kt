package ink.pmc.common.member.api.punishment

import ink.pmc.common.member.api.Member
import java.time.Instant

@Suppress("UNUSED")
interface Punishment {

    val id: Long
    val type: PunishmentType
    val time: Instant
    val belongs: Member
    val isRevoked: Boolean

    suspend fun executor(): Member

}