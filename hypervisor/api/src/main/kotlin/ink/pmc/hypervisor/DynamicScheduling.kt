package ink.pmc.hypervisor

import ink.pmc.utils.inject.inlinedGet
import org.bukkit.entity.Player

interface DynamicScheduling {
    companion object : DynamicScheduling by inlinedGet()

    val enabled: Boolean
    val state: SchedulingState
    val isRunning: Boolean

    fun start()

    fun stop()

    fun setDynamicViewDistanceState(player: Player, state: Boolean)

    fun setDynamicViewDistanceStateLocally(player: Player, state: Boolean)

    fun toggleDynamicViewDistance(player: Player)

    fun isDynamicViewDistanceEnabled(player: Player): Boolean

    fun isDynamicViewDistanceEnabledLocally(player: Player): Boolean
}