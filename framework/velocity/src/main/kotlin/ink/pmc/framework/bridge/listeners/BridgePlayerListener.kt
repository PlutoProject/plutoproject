package ink.pmc.framework.bridge.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import ink.pmc.framework.bridge.BridgeRpc
import ink.pmc.framework.bridge.InternalServer
import ink.pmc.framework.bridge.player.ProxyLocalPlayer
import ink.pmc.framework.bridge.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.proto.playerInfo
import ink.pmc.framework.bridge.proxyBridge
import ink.pmc.framework.bridge.server.localServer
import kotlin.jvm.optionals.getOrNull

object BridgePlayerListener {
    @Subscribe
    fun LoginEvent.e() {
        localServer.players.add(ProxyLocalPlayer(player))
    }

    @Subscribe
    suspend fun ServerConnectedEvent.e() {
        val current = proxyBridge.getServer(server.serverInfo.name) as InternalServer? ?: error("Server not registered")
        val remotePlayer = proxyBridge.getRemotePlayer(player.uniqueId) ?: ProxyRemoteBackendPlayer(player, current)
        val previous = previousServer.getOrNull()?.let { proxyBridge.getServer(it.serverInfo.name) } as InternalServer?
        current.players.add(remotePlayer)
        previous?.players?.remove(remotePlayer)
        BridgeRpc.notify(notification {
            playerInfoUpdate = playerInfo {
                server = current.id
                uniqueId = player.uniqueId.toString()
            }
        })
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