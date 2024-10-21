package ink.pmc.hypervisor.config

import org.bukkit.entity.SpawnCategory

typealias CurveSampler = Map<Int, Int>
typealias SpawnStrategy = Map<SpawnCategory, CurveSampler>

data class DynamicScheduling(
    val enabled: Boolean,
    val cyclePeriod: Long,
    val viewDistance: ViewDistance,
    val simulateDistance: SimulateDistance,
    val spawnLimits: SpawnSettings,
    val ticksPerSpawn: SpawnSettings,
)

data class ViewDistance(
    val enabled: Boolean,
    val virtualHosts: List<String>,
    val maximumPing: Double,
    val sampler: CurveSampler
)

data class SimulateDistance(
    val enabled: Boolean,
    val sampler: CurveSampler,
)

data class SpawnSettings(
    val enabled: Boolean,
    val default: SpawnStrategy,
    val world: Map<String, SpawnStrategy>
)