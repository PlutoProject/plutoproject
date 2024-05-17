package ink.pmc.common.exchange.lobby

import ink.pmc.common.exchange.lobbySpawnLocation
import ink.pmc.common.exchange.utils.applyExchangeStatus
import ink.pmc.common.utils.platform.threadSafeTeleport
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