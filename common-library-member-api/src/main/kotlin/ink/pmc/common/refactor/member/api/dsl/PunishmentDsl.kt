package ink.pmc.common.refactor.member.api.dsl

import ink.pmc.common.refactor.member.api.punishment.Approvable
import ink.pmc.common.refactor.member.api.punishment.Punishments

@Suppress("UNUSED")
class PunishmentDsl : Approvable() {

    var type: Punishments? = null

}