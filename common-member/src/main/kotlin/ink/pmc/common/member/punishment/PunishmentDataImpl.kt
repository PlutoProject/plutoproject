package ink.pmc.common.member.punishment

import ink.pmc.common.member.api.punishment.PunishmentData

class PunishmentDataImpl : PunishmentData {

    override val punishments: MutableCollection<PunishmentImpl> = mutableSetOf()
    override var currentPunishment: Long? = null
    override var lastPunishment: Long? = null

}