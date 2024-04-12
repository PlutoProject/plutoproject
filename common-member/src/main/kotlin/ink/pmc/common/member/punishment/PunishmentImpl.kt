package ink.pmc.common.member.punishment

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentType
import java.time.LocalDateTime

class PunishmentImpl : Punishment {

    override val id: Long
        get() = TODO("Not yet implemented")
    override val type: PunishmentType
        get() = TODO("Not yet implemented")
    override val time: LocalDateTime
        get() = TODO("Not yet implemented")
    override val belongs: Member
        get() = TODO("Not yet implemented")
    override val isRevoked: Boolean
        get() = TODO("Not yet implemented")
    override val executor: Member
        get() = TODO("Not yet implemented")

}