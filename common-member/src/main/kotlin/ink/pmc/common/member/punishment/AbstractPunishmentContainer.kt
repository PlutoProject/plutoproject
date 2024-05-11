package ink.pmc.common.member.punishment

import ink.pmc.common.member.api.punishment.PunishmentContainer

abstract class AbstractPunishmentContainer : PunishmentContainer {

    override fun revoke(punishmentId: Long) {
        if (!exist(punishmentId)) {
            return
        }

        revoke(get(punishmentId)!!)
    }

    abstract fun reload()

    abstract fun save()

}