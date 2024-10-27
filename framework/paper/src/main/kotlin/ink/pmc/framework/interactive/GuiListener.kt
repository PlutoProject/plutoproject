package ink.pmc.framework.interactive

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED", "UnusedReceiverParameter")
object GuiListener : Listener, KoinComponent {
    private val manager by inject<GuiManager>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun PlayerJoinEvent.e() {
        manager.dispose(player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun PlayerQuitEvent.e() {
        manager.dispose(player)
    }
}