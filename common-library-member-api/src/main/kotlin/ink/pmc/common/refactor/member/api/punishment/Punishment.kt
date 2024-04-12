package ink.pmc.common.refactor.member.api.punishment

import ink.pmc.common.refactor.member.api.Member
import java.time.LocalDateTime

@Suppress("UNUSED")
interface Punishment {

    val id: Long
    val type: PunishmentType
    val time: LocalDateTime
    val belongs: Member
    val isRevoked: Boolean
    val executor: Member

}