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
        val before = viewDistance
        when {
            DynamicScheduling.getDynamicViewDistanceLocally(this) == DynamicViewDistanceState.ENABLED
                    && viewDistance < configured -> viewDistance = configured

            DynamicScheduling.getDynamicViewDistanceLocally(this) != DynamicViewDistanceState.ENABLED
                    && viewDistance > paper.viewDistance -> viewDistance = paper.viewDistance
        }
        if (viewDistance != before) {
            pluginLogger.info("Updated $name's view-distance: $before -> $viewDistance")
        }
    }

    @EventHandler
    fun PlayerJoinEvent.e() {
        val vhosts = config.dynamicScheduling.viewDistance.virtualHosts
        val vhost = player.virtualHost
        if (player.virtualHost != null && !vhosts.contains("${vhost?.hostString}:${vhost?.port}")) {
            DynamicScheduling.setDynamicViewDistanceLocally(player, DynamicViewDistanceState.DISABLED_DUE_VHOST)
            pluginLogger.info("Disabled ${player.name}'s view-distance because the virtual host is not in whitelist")
            return
        }
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