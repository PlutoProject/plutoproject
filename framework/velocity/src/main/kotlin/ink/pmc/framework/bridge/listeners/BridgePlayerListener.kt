package ink.pmc.framework.bridge.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import ink.pmc.framework.bridge.*
import ink.pmc.framework.bridge.player.ProxyLocalPlayer
import ink.pmc.framework.bridge.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.server.localServer
import kotlin.jvm.optionals.getOrNull

object BridgePlayerListener {
    @Subscribe
    suspend fun LoginEvent.e() {
        val localPlayer = ProxyLocalPlayer(player)
        localServer.players.add(localPlayer)
        BridgeRpc.notify(notification {
            playerInfoUpdate = localPlayer.toInfo()
        })
    }

    @Subscribe
    suspend fun ServerConnectedEvent.e() {
        val current = proxyBridge.getServer(server.serverInfo.name) as InternalServer? ?: error("Server not registered")
        val remotePlayer = proxyBridge.getRemotePlayer(player.uniqueId) as InternalPlayer?
            ?: ProxyRemoteBackendPlayer(player, current)
        val previous = previousServer.getOrNull()?.let { proxyBridge.getServer(it.serverInfo.name) } as InternalServer?
        remotePlayer.server = current
        current.players.add(remotePlayer)
        previous?.players?.remove(remotePlayer)
        BridgeRpc.notify(notification {
            playerInfoUpdate = remotePlayer.toInfo()
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