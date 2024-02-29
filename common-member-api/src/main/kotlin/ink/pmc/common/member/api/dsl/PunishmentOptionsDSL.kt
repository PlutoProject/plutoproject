package ink.pmc.common.member.api.dsl

import ink.pmc.common.member.api.punishment.PunishmentReason
import ink.pmc.common.member.api.punishment.PunishmentType

class PunishmentOptionsDSL {

    var type: PunishmentType? = null
    var reason: PunishmentReason = PunishmentReason.NONE

}