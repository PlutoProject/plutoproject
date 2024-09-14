package ink.pmc.essentials.listeners

import ink.pmc.essentials.api.back.BackManager
import ink.pmc.essentials.api.home.HomeTeleportEvent
import ink.pmc.essentials.api.teleport.RequestState
import ink.pmc.essentials.api.teleport.RequestStateChangeEvent
import ink.pmc.essentials.api.teleport.random.RandomTeleportEvent
import ink.pmc.essentials.api.warp.WarpTeleportEvent
import ink.pmc.utils.concurrent.submitAsync
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED", "UnusedReceiverParameter")
object BackListener : Listener, KoinComponent {

    private val manager by inject<BackManager>()

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun HomeTeleportEvent.e() {
        submitAsync {
            manager.set(player, from)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun WarpTeleportEvent.e() {
        submitAsync {
            manager.set(player, from)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun RandomTeleportEvent.e() {
        submitAsync {
            manager.set(player, from)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun PlayerDeathEvent.e() {
        submitAsync {
            manager.set(player, player.location)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun RequestStateChangeEvent.e() {
        if (after != RequestState.ACCEPTED) return
        submitAsync {
            val player = request.source
            manager.set(player, player.location)
        }
    }

}