package ink.pmc.common.utils.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.utils.platform.proxy

fun Player.switchServer(name: String) {
    val velocityServer = proxy.getServer(name).get()
    this.createConnectionRequest(velocityServer).fireAndForget()
}