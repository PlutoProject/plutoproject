package ink.pmc.framework.bridge.backend.listeners

import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.backend.player.BackendLocalPlayer
import ink.pmc.framework.bridge.backend.server.localServer
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.localPlayerNotFound
import ink.pmc.framework.bridge.localWorldNotFound
import ink.pmc.framework.bridge.player.createInfoWithoutLocation
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UnusedReceiverParameter")
object BridgePlayerListener : Listener {
    @EventHandler
    suspend fun PlayerJoinEvent.e() {
        val localPlayer = BackendLocalPlayer(player, localServer)
        localServer.players.add(localPlayer)
        val remotePlayer = internalBridge.getPlayer(player.uniqueId, ServerState.REMOTE, ServerType.BACKEND)
        // 玩家切换到本服
        if (remotePlayer != null) {
            internalBridge.removeRemoteBackendPlayer(remotePlayer.uniqueId)
        }
        bridgeStub.updatePlayerInfo(localPlayer.createInfoWithoutLocation())
    }

    @EventHandler
    suspend fun PlayerChangedWorldEvent.e() {
        val localPlayer = internalBridge.getInternalLocalPlayer(player.uniqueId)
            ?: localWorldNotFound(player.world.name)
        bridgeStub.updatePlayerInfo(localPlayer.createInfoWithoutLocation())
    }

    @EventHandler
    fun PlayerQuitEvent.e() {
        val localPlayer = internalBridge.getInternalLocalPlayer(player.uniqueId)
            ?: localPlayerNotFound(player.name)
        localServer.players.remove(localPlayer)
    }
}