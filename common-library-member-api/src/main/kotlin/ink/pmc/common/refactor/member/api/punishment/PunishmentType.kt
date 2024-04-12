package ink.pmc.common.refactor.member.api.punishment

import ink.pmc.common.member.api.BAN_PUNISHMENT
import ink.pmc.common.member.api.REMOVE_WHITELIST_PUNISHMENT
import ink.pmc.common.member.api.WARN_PUNISHMENT
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
enum class PunishmentType(val display: Component) {

    BAN(BAN_PUNISHMENT), WARN(WARN_PUNISHMENT), REMOVE_WHITELIST(REMOVE_WHITELIST_PUNISHMENT)

}