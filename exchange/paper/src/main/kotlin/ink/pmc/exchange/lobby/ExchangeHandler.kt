package ink.pmc.exchange.lobby

import ink.pmc.exchange.paper.lobbySpawnLocation
import ink.pmc.exchange.utils.applyExchangeStatus
import ink.pmc.utils.platform.threadSafeTeleport
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("UNUSED")
object ExchangeHandler : Listener {

    @EventHandler
    fun playerJoinEvent(event: PlayerJoinEvent) {
        val player = event.player
        applyExchangeStatus(player)
        player.threadSafeTeleport(lobbySpawnLocation)
    }
}