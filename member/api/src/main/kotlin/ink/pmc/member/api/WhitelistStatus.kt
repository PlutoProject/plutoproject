package ink.pmc.member.api

import net.kyori.adventure.text.Component

@Suppress("UNUSED")
enum class WhitelistStatus(val display: Component) {

    NON_WHITELISTED(STATUS_NON_WHITELISTED),
    WHITELISTED(STATUS_WHITELISTED),
    WHITELISTED_BEFORE(STATUS_WHITELISTED_BEFORE)
}