package ink.pmc.common.member.api.punishment

import java.util.*

interface Punishment {

    val id: Long
    val owner: UUID
    val type: PunishmentType
    val reason: PunishmentReason
    val executeDate: Date
    var isPardoned: Boolean
    var pardonReason: PardonReason?

}