package ink.pmc.hypervisor

import ink.pmc.hypervisor.config.HypervisorConfig
import ink.pmc.utils.platform.paper
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED")
object DynamicSchedulingListener : Listener, KoinComponent {
    private val config by inject<HypervisorConfig>()

    private fun Player.refreshViewDistance() {
        val configured = config.dynamicScheduling.viewDistance.value
        viewDistance = if (DynamicScheduling.isDynamicViewDistanceEnabledLocally(this) && viewDistance < configured) {
            configured
        } else {
            paper.viewDistance
        }
    }

    @EventHandler
    fun PlayerJoinEvent.e() {
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerTeleportEvent.e() {
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerRespawnEvent.e() {
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerChangedWorldEvent.e() {
        player.refreshViewDistance()
    }
}