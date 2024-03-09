package ink.pmc.common.member.api.punishment

data class PunishmentOptions(
    var type: PunishmentType,
) {

    var reason: PunishmentReason = PunishmentReason.NONE

}