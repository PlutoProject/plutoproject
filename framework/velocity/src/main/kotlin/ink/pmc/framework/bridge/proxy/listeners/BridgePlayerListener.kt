package ink.pmc.framework.bridge.proxy.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.player.createInfo
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.proto.playerDisconnect
import ink.pmc.framework.bridge.proto.playerSwitchServer
import ink.pmc.framework.bridge.proxy.BridgeRpc
import ink.pmc.framework.bridge.proxy.player.ProxyLocalPlayer
import ink.pmc.framework.bridge.proxy.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proxy.server.localServer
import ink.pmc.framework.bridge.server.InternalServer
import kotlin.jvm.optionals.getOrNull

object BridgePlayerListener {
    @Subscribe
    suspend fun LoginEvent.e() {
        val localPlayer = ProxyLocalPlayer(player, localServer)
        localServer.players.add(localPlayer)
        BridgeRpc.notify(notification {
            playerJoin = localPlayer.createInfo()
        })
    }

    @Subscribe
    suspend fun ServerConnectedEvent.e() {
        val current = internalBridge.getInternalRemoteServer(server.serverInfo.name)
        val remotePlayer = internalBridge.getRemotePlayer(player.uniqueId) as InternalPlayer?
            ?: ProxyRemoteBackendPlayer(player, current, null)
        val previous = previousServer.getOrNull()?.let { internalBridge.getInternalRemoteServer(it.serverInfo.name) }
        remotePlayer.server = current
        current.players.add(remotePlayer)
        previous?.players?.remove(remotePlayer)
        BridgeRpc.notify(notification {
            playerSwitchServer = playerSwitchServer {
                server = current.id
                playerUuid = remotePlayer.uniqueId.toString()
            }
        })
    }

    @Subscribe
    suspend fun DisconnectEvent.e() {
        val uniqueId = player.uniqueId
        internalBridge.servers.forEach {
            val server = it as InternalServer
            server.players.removeIf { player -> player.uniqueId == uniqueId }
        }
        BridgeRpc.notify(notification {
            playerDisconnect = playerDisconnect {
                this.uniqueId = uniqueId.toString()
            }
        })
    }
}