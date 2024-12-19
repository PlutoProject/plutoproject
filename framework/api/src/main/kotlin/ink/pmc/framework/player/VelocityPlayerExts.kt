package ink.pmc.framework.player

import com.velocitypowered.api.proxy.ConnectionRequestBuilder
import com.velocitypowered.api.proxy.Player
import ink.pmc.framework.platform.proxy
import kotlinx.coroutines.future.await

suspend fun Player.switchServer(name: String): ConnectionRequestBuilder.Result {
    val velocityServer = proxy.getServer(name).get()
    val future = this.createConnectionRequest(velocityServer).connect()
    return future.await()
}