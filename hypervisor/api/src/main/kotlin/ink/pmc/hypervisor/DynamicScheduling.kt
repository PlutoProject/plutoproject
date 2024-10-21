package ink.pmc.hypervisor

import ink.pmc.utils.inject.inlinedGet
import org.bukkit.entity.Player
import org.bukkit.entity.SpawnCategory

interface DynamicScheduling {
    companion object : DynamicScheduling by inlinedGet()

    val enabled: Boolean
    val currentSimulateDistance: Int
    val currentSpawnLimits: Map<SpawnCategory, Int>
    val currentTicksPerSpawn: Map<SpawnCategory, Int>
    val isRunning: Boolean

    fun start()

    fun stop()

    fun setDynamicViewDistance(player: Player, state: Boolean)

    fun setDynamicViewDistanceLocally(player: Player, state: DynamicViewDistanceState)

    fun toggleDynamicViewDistance(player: Player)

    fun getDynamicViewDistance(player: Player): Boolean

    fun getDynamicViewDistanceLocally(player: Player): DynamicViewDistanceState
}