package ink.pmc.framework.bridge.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import ink.pmc.framework.bridge.BridgeRpc
import ink.pmc.framework.bridge.InternalServer
import ink.pmc.framework.bridge.player.ProxyLocalPlayer
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.proxyBridge
import ink.pmc.framework.bridge.server.localServer

object BridgePlayerListener {
    @Subscribe
    fun LoginEvent.e() {
        localServer.players.add(ProxyLocalPlayer(player))
    }

    @Subscribe
    suspend fun DisconnectEvent.e() {
        proxyBridge.servers.forEach {
            (it as InternalServer).players.removeIf { player -> player.uniqueId == player.uniqueId }
        }
        BridgeRpc.notify(notification {
            playerDisconnectUuid = player.uniqueId.toString()
        })
    }
}