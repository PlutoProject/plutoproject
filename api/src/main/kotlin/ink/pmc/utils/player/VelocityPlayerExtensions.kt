package ink.pmc.utils.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.utils.platform.proxy
import kotlinx.coroutines.future.await

suspend fun Player.switchServer(name: String) {
    val velocityServer = proxy.getServer(name).get()
    val future = this.createConnectionRequest(velocityServer).connect()
    future.await()
}