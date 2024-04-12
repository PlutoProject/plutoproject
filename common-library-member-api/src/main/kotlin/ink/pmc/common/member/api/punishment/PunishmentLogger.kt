package ink.pmc.common.member.api.punishment

import ink.pmc.common.member.api.Member

@Suppress("UNUSED")
interface PunishmentLogger {

    val historyPunishments: Collection<Punishment>
    val lastPunishment: Punishment

    fun create(type: PunishmentType, executor: Member): Punishment

    fun revoke(punishment: Punishment)

    fun revoke(punishmentId: Long)

}