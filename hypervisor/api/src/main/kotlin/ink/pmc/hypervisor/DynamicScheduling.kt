package ink.pmc.hypervisor

import org.bukkit.entity.Player

interface DynamicScheduling {
    val enabled: Boolean

    fun setDynamicViewDistanceState(player: Player, state: Boolean)

    fun toggleDynamicViewDistance(player: Player)

    fun isDynamicViewDistanceEnabled(player: Player): Boolean
}