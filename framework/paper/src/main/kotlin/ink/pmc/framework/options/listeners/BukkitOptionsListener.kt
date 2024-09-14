package ink.pmc.framework.options.listeners

import ink.pmc.framework.options.OptionsManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UNUSED", "UNUSED_PARAMETER", "UnusedReceiverParameter")
object BukkitOptionsListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    suspend fun PlayerJoinEvent.e() {
        OptionsManager.getOptions(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    suspend fun PlayerQuitEvent.e() {
        if (!OptionsManager.isPlayerLoaded(player.uniqueId)) return
        OptionsManager.save(player.uniqueId)
        OptionsManager.unloadPlayer(player.uniqueId)
    }
}