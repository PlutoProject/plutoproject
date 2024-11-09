package ink.pmc.framework.bridge.backend.listeners

import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.backend.player.BackendLocalPlayer
import ink.pmc.framework.bridge.backend.server.localServer
import ink.pmc.framework.bridge.player.toInfo
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UnusedReceiverParameter")
object BridgePlayerListener : Listener {
    @EventHandler
    suspend fun PlayerJoinEvent.e() {
        val localPlayer = BackendLocalPlayer(player)
        localServer.players.add(localPlayer)
        bridgeStub.updatePlayerInfo(localPlayer.toInfo())
    }

    @EventHandler
    fun PlayerQuitEvent.e() {
        val localPlayer = localServer.getPlayer(player.uniqueId) ?: error("Local player not found: ${player.name}")
        localServer.players.remove(localPlayer)
    }
}