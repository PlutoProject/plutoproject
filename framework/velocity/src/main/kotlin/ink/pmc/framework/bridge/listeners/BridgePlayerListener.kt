package ink.pmc.framework.bridge.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import ink.pmc.framework.bridge.server.localServer
import ink.pmc.framework.bridge.player.ProxyLocalPlayer

object BridgePlayerListener {
    @Subscribe
    fun LoginEvent.e() {
        localServer.players.add(ProxyLocalPlayer(player))
    }

    @Subscribe
    fun DisconnectEvent.e() {
        localServer.players.removeIf { it.uniqueId == player.uniqueId }
    }
}