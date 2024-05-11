package ink.pmc.common.member.api.punishment

import ink.pmc.common.member.api.Member

@Suppress("UNUSED")
interface PunishmentContainer {

    val punishments: Collection<Punishment>
    val lastPunishment: Punishment?

    fun create(type: PunishmentType, executor: Member?): Punishment

    operator fun get(id: Long): Punishment?

    fun exist(id: Long): Boolean

    fun revoke(punishment: Punishment)

    fun revoke(punishmentId: Long)

}