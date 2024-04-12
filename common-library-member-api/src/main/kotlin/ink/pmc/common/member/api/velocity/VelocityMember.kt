package ink.pmc.common.member.api.velocity

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.member.api.Member
import ink.pmc.common.utils.platform.velocityProxyServer

@Suppress("UNUSED")
val Member.player: Player?
    get() {
        val optional = velocityProxyServer.getPlayer(this.id)

        if (!optional.isEmpty) {
            return optional.get()
        }

        return null
    }