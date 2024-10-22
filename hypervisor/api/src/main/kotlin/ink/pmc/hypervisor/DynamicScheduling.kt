package ink.pmc.hypervisor

import ink.pmc.utils.inject.inlinedGet
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.entity.SpawnCategory

interface DynamicScheduling {
    companion object : DynamicScheduling by inlinedGet()

    val enabled: Boolean
    val viewDistanceEnabled: Boolean
    val simulateDistanceEnabled: Boolean
    val spawnLimitsEnabled: Boolean
    val ticksPerSpawnEnabled: Boolean
    val currentSimulateDistance: Int
    val currentSpawnLimits: Map<SpawnCategory, Int>
    val currentTicksPerSpawn: Map<SpawnCategory, Int>
    val isRunning: Boolean

    fun start()

    fun stop()

    fun setViewDistance(player: Player, state: Boolean)

    fun setViewDistanceLocally(player: Player, state: DynamicViewDistanceState)

    fun toggleViewDistance(player: Player)

    fun getViewDistance(player: Player): Boolean

    fun getViewDistanceLocally(player: Player): DynamicViewDistanceState

    fun removeLocalViewDistanceState(player: Player)

    fun calculateCurves()

    fun getSimulateDistanceWhen(millsPerSecond: Double): Int

    fun getSimulateDistanceCurve(): PolynomialFunction

    fun getSpawnLimitsWhen(millsPerSecond: Double, world: World? = null, category: SpawnCategory): Int

    fun getSpawnLimitsCurve(world: World? = null, category: SpawnCategory): PolynomialFunction?

    fun getTicksPerSpawnWhen(millsPerSecond: Double, world: World? = null, category: SpawnCategory): Int

    fun getTicksPerSecondCurve(world: World? = null, category: SpawnCategory): PolynomialFunction?
}
