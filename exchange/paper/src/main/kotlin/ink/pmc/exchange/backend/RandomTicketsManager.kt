package ink.pmc.exchange.backend

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.exchange.paperExchangePlugin
import ink.pmc.member.api.paper.member
import ink.pmc.utils.platform.paper
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

@Suppress("UNUSED")
class RandomTicketsManager : Listener {

    private val managedPlayers = mutableMapOf<UUID, RandomTicketsHandler>()

    private suspend fun manage(player: Player) {
        if (isManaged(player)) {
            return
        }

        val handler = RandomTicketsHandler(player, player.member())
        managedPlayers[player.uniqueId] = handler
        paper.pluginManager.registerSuspendingEvents(handler, paperExchangePlugin)
    }

    private fun unmanage(player: Player) {
        if (!isManaged(player)) {
            return
        }

        val handler = managedPlayers[player.uniqueId]!!
        HandlerList.unregisterAll(handler)
        managedPlayers.remove(player.uniqueId)
    }

    private fun isManaged(player: Player): Boolean {
        return managedPlayers.containsKey(player.uniqueId)
    }

    @EventHandler
    suspend fun playerJoinEvent(event: PlayerJoinEvent) {
        manage(event.player)
    }

    @EventHandler
    fun playerQuitEvent(event: PlayerQuitEvent) {
        unmanage(event.player)
    }
}