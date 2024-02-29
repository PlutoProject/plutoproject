package ink.pmc.common.member.api.punishment

interface PunishmentData {

    val punishments: MutableCollection<out Punishment>
    var currentPunishment: Long?
    var lastPunishment: Long?

    fun currentPunishmentObj(): Punishment? =
        if (!punishments.any { it.id == currentPunishment }) null else punishments.first { it.id == currentPunishment }

    fun lastPunishmentObj(): Punishment? =
        if (!punishments.any { it.id == lastPunishment }) null else punishments.first { it.id == lastPunishment }

}