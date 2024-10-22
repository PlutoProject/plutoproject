package ink.pmc.hypervisor.listeners

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import ink.pmc.hypervisor.DynamicScheduling
import ink.pmc.hypervisor.DynamicViewDistanceState
import ink.pmc.hypervisor.config.HypervisorConfig
import ink.pmc.hypervisor.pluginLogger
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Suppress("UNUSED")
object DynamicViewDistanceListener : Listener, KoinComponent {
    private val config by lazy { get<HypervisorConfig>().dynamicScheduling }

    private fun Player.refreshViewDistance() {
        val boost = config.viewDistance.boost
        val standard = config.viewDistance.standard
        val before = viewDistance
        // 不知道为什么在设置 viewDistance 后，立即获取出来的值并不是刚刚设置的
        // 所以缓存一下
        var after = before
        when {
            DynamicScheduling.getViewDistanceLocally(this) == DynamicViewDistanceState.ENABLED
                    && viewDistance < boost -> {
                after = boost
                // 设置之后会变成指定值 -1，所以输出可能和预期不同
                // 暂时不清楚是不是 Paper 的问题，不在此处做处理
                viewDistance = after
            }

            DynamicScheduling.getViewDistanceLocally(this) != DynamicViewDistanceState.ENABLED
                    && viewDistance > standard -> {
                after = standard
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
        val vhosts = config.viewDistance.virtualHosts
        val vhost = player.formattedVhost
        if (vhost != null && !vhosts.contains(vhost)) {
            DynamicScheduling.setViewDistanceLocally(player, DynamicViewDistanceState.DISABLED_DUE_VHOST)
        }
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerTeleportEvent.e() {
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerPostRespawnEvent.e() {
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerChangedWorldEvent.e() {
        player.refreshViewDistance()
    }

    @EventHandler
    fun PlayerQuitEvent.e() {
        DynamicScheduling.removeLocalViewDistanceState(player)
    }
}