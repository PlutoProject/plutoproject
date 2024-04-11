package ink.pmc.common.refactor.member.api.punishment

import java.time.LocalDateTime

@Suppress("UNUSED")
interface PunishmentLog {

    val id: Long
    val time: LocalDateTime
    val type: Punishments
    val approvers: Collection<Long>
    val isPardoned: Boolean
    val pardonApprovers: Collection<Long>

}