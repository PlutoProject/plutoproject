package ink.pmc.common.member.punishment

import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.storage.PunishmentStorage

abstract class AbstractPunishment : Punishment {

    abstract val storage: PunishmentStorage

    override fun equals(other: Any?): Boolean {
        if (other !is Punishment) {
            return false
        }

        return other.id == id
    }

    override fun hashCode(): Int {
        return storage.hashCode()
    }

}