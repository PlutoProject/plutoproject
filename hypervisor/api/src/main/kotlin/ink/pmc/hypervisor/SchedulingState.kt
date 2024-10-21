package ink.pmc.hypervisor

import org.bukkit.entity.SpawnCategory

data class SchedulingState(
    val simulateDistance: Int,
    val spawnLimits: Map<SpawnCategory, Int>,
    val ticksPerSpawn: Map<SpawnCategory, Int>
)