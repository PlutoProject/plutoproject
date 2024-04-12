package ink.pmc.common.member.punishment

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentLogger
import ink.pmc.common.member.api.punishment.PunishmentType

class PunishmentLoggerImpl : PunishmentLogger {

    override val historyPunishments: Collection<Punishment>
        get() = TODO("Not yet implemented")
    override val lastPunishment: Punishment
        get() = TODO("Not yet implemented")

    override fun create(type: PunishmentType, executor: Member): Punishment {
        TODO("Not yet implemented")
    }

    override fun revoke(punishment: Punishment) {
        TODO("Not yet implemented")
    }

    override fun revoke(punishmentId: Long) {
        TODO("Not yet implemented")
    }

}