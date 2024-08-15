package ink.pmc.essentials.listeners

import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.utils.concurrent.submitAsync
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.server.ServerLoadEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED", "UnusedReceiverParameter")
object WarpListener : Listener, KoinComponent {

    private val manager by inject<WarpManager>()

    @EventHandler
    fun ServerLoadEvent.e() {
        submitAsync {
            manager.list()
        }
    }

    @EventHandler
    suspend fun PlayerJoinEvent.e() {
        if (manager.getPreferredSpawn(player) != null) return
        val default = manager.getDefaultSpawn() ?: return
        manager.setPreferredSpawn(player, default)
    }

}