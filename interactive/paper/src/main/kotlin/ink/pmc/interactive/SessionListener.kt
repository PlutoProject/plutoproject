package ink.pmc.interactive

import ink.pmc.interactive.api.Interactive
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED", "UnusedReceiverParameter")
object SessionListener : Listener, KoinComponent {

    private val manager by inject<Interactive>()

    @EventHandler
    fun PlayerJoinEvent.e() {
        manager.get(player)?.close()
    }

    @EventHandler
    fun PlayerQuitEvent.e() {
        manager.get(player)?.close()
    }

}