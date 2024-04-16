package ink.pmc.common.server.velocity

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.server.player.ServerPlayer
import ink.pmc.common.utils.platform.proxy

val ServerPlayer.player: Player
    get() {
        val optional = proxy.getPlayer(this.uniqueID)
        return optional.get()
    }