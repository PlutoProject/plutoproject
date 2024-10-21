package ink.pmc.hypervisor

import ink.pmc.utils.inject.inlinedGet
import org.bukkit.entity.Player

interface DynamicScheduling {
    companion object : DynamicScheduling by inlinedGet()

    val enabled: Boolean

    fun setDynamicViewDistanceState(player: Player, state: Boolean)

    fun toggleDynamicViewDistance(player: Player)

    fun isDynamicViewDistanceEnabled(player: Player): Boolean
}