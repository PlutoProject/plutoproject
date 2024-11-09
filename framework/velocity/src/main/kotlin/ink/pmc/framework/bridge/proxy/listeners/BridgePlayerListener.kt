package ink.pmc.framework.bridge.proxy.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import ink.pmc.framework.bridge.*
import ink.pmc.framework.bridge.proxy.player.ProxyLocalPlayer
import ink.pmc.framework.bridge.proxy.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.proxy.BridgeRpc
import ink.pmc.framework.bridge.proxy.proxyBridge
import ink.pmc.framework.bridge.proxy.server.localServer
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
        val current = proxyBridge.getServer(server.serverInfo.name) as InternalServer?
            ?: error("Server not found: ${server.serverInfo.name}")
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
        val uniqueId = player.uniqueId
        val remotePlayer = proxyBridge.getRemotePlayer(uniqueId) as InternalPlayer?
        remotePlayer?.isOnline = false
        proxyBridge.servers.forEach {
            val server = it as InternalServer
            server.players.removeIf { player -> player.uniqueId == uniqueId }
        }
        BridgeRpc.notify(notification {
            playerDisconnectUuid = player.uniqueId.toString()
        })
    }
}