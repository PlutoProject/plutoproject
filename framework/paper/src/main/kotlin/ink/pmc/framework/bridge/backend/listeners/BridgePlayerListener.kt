package ink.pmc.framework.bridge.backend.listeners

import ink.pmc.framework.bridge.*
import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.backend.player.BackendLocalPlayer
import ink.pmc.framework.bridge.backend.server.localServer
import ink.pmc.framework.bridge.player.createInfoWithoutLocation
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UnusedReceiverParameter")
object BridgePlayerListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    suspend fun PlayerLoginEvent.e() {
        val localPlayer = BackendLocalPlayer(player, localServer)
        localServer.players.add(localPlayer)
        val remotePlayer = internalBridge.getPlayer(player.uniqueId, ServerState.REMOTE, ServerType.BACKEND)
        // 玩家切换到本服
        if (remotePlayer != null) {
            internalBridge.removeRemoteBackendPlayer(remotePlayer.uniqueId)
        }
        val result = bridgeStub.updatePlayerInfo(localPlayer.createInfoWithoutLocation())
        checkCommonResult(result)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    suspend fun PlayerChangedWorldEvent.e() {
        val localPlayer = internalBridge.getInternalLocalPlayer(player.uniqueId)
            ?: return warn { throwLocalWorldNotFound(player.world.name) }
        val result = bridgeStub.updatePlayerInfo(localPlayer.createInfoWithoutLocation())
        checkCommonResult(result)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun PlayerQuitEvent.e() {
        val localPlayer = internalBridge.getInternalLocalPlayer(player.uniqueId)
            ?: return warn { throwLocalPlayerNotFound(player.name) }
        localServer.players.remove(localPlayer)
    }
}