package ink.pmc.common.utils.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.utils.platform.velocityProxyServer

fun Player.switchServer(name: String) {
    val velocityServer = velocityProxyServer.getServer(name).get()
    this.createConnectionRequest(velocityServer).fireAndForget()
}