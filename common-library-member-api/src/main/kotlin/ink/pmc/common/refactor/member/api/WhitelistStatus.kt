package ink.pmc.common.refactor.member.api

import ink.pmc.common.member.api.STATUS_NON_WHITELISTED
import ink.pmc.common.member.api.STATUS_WHITELISTED
import ink.pmc.common.member.api.STATUS_WHITELISTED_BEFORE
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
enum class WhitelistStatus(val display: Component) {

    NON_WHITELISTED(STATUS_NON_WHITELISTED),
    WHITELISTED(STATUS_WHITELISTED),
    WHITELISTED_BEFORE(STATUS_WHITELISTED_BEFORE)

}