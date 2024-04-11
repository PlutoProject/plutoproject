package ink.pmc.common.refactor.member.api

import ink.pmc.common.member.api.LITTLESKIN_AUTH
import ink.pmc.common.member.api.OFFICIAL_AUTH
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
enum class AuthType(val display: Component) {

    OFFICIAL(OFFICIAL_AUTH), LITTLESKIN(LITTLESKIN_AUTH);

    val isOfficial
        get() = this == OFFICIAL

}