package ink.pmc.framework.bridge.proxy.listeners

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.player.createInfoWithoutLocation
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.proto.playerDisconnect
import ink.pmc.framework.bridge.proxy.BridgeRpc
import ink.pmc.framework.bridge.proxy.player.ProxyLocalPlayer
import ink.pmc.framework.bridge.proxy.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proxy.server.localServer
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.bridge.throwRemoteServerNotFound
import ink.pmc.framework.bridge.warn

object BridgePlayerListener {
    @Subscribe(order = PostOrder.FIRST)
    suspend fun LoginEvent.e() {
        val localPlayer = ProxyLocalPlayer(player, localServer)
        localServer.players.add(localPlayer)
        BridgeRpc.notify(notification {
            playerJoin = localPlayer.createInfoWithoutLocation()
        })
    }

    @Subscribe(order = PostOrder.FIRST)
    suspend fun ServerPreConnectEvent.e() {
        val current = internalBridge.getInternalRemoteServer(originalServer.serverInfo.name)
            ?: return warn { throwRemoteServerNotFound(originalServer.serverInfo.name) }
        val remotePlayer = internalBridge.getInternalRemoteBackendPlayer(player.uniqueId)
            ?: ProxyRemoteBackendPlayer(player, current, null)
        val previous = previousServer?.let { internalBridge.getInternalRemoteServer(it.serverInfo.name) }
        previous?.players?.remove(remotePlayer)
        remotePlayer.server = current
        current.players.add(remotePlayer)
        BridgeRpc.notify(notification {
            playerSwitchServer = remotePlayer.createInfoWithoutLocation()
        })
    }

    @Subscribe(order = PostOrder.LAST)
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