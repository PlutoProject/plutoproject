package ink.pmc.hypervisor.config

import org.bukkit.entity.SpawnCategory

typealias Double2IntCurve = Map<Double, Int>
typealias SpawnStrategy = Map<SpawnCategory, Double2IntCurve>

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
    val value: Int
)

data class SimulateDistance(
    val enabled: Boolean,
    val curve: Double2IntCurve,
)

data class SpawnSettings(
    val enabled: Boolean,
    val default: SpawnStrategy,
    val world: Map<String, SpawnStrategy>
)