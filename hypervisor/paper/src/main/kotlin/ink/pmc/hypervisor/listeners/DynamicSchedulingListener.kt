package ink.pmc.hypervisor.listeners

import ink.pmc.hypervisor.DynamicScheduling
import ink.pmc.hypervisor.DynamicViewDistanceState
import ink.pmc.hypervisor.config.HypervisorConfig
import ink.pmc.hypervisor.pluginLogger
import ink.pmc.utils.platform.paper
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED")
object DynamicSchedulingListener : Listener, KoinComponent {
    private val config by inject<HypervisorConfig>()

    private fun Player.refreshViewDistance() {
        val configured = config.dynamicScheduling.viewDistance.value
        val before = viewDistance
        // 不知道为什么在设置 viewDistance 后，立即获取出来的值并不是刚刚设置的
        // 所以缓存一下
        var after = before
        when {
            DynamicScheduling.getDynamicViewDistanceLocally(this) == DynamicViewDistanceState.ENABLED
                    && viewDistance < configured -> {
                after = configured
                viewDistance = after
            }

            DynamicScheduling.getDynamicViewDistanceLocally(this) != DynamicViewDistanceState.ENABLED
                    && viewDistance > paper.viewDistance -> {
                after = paper.viewDistance
                viewDistance = after
            }
        }
        if (before != after) {
            pluginLogger.info("Update $name's view distance: $before -> $after")
        }
    }

    private val Player.formattedVhost: String?
        get() {
            /*
            return this.virtualHost?.let {
                val host = it.hostString.takeIf { str -> str != "<unresolved>" } ?: it.address?.hostAddress
                "$host:${it.port}"
            }
            */
            return virtualHost?.let {
                "${it.hostString}:${it.port}"
            }
        }

    @EventHandler
    fun PlayerJoinEvent.e() {
        val vhosts = config.dynamicScheduling.viewDistance.virtualHosts
        val vhost = player.formattedVhost
        if (vhost != null && !vhosts.contains(vhost)) {
            DynamicScheduling.setDynamicViewDistanceLocally(player, DynamicViewDistanceState.DISABLED_DUE_VHOST)
            return
        }
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerTeleportEvent.e() {
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerDeathEvent.e() {
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerChangedWorldEvent.e() {
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerQuitEvent.e() {
        DynamicScheduling.removeLocalDynamicViewDistanceState(player)
    }
}