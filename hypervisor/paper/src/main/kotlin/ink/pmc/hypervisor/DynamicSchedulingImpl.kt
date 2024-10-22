package ink.pmc.hypervisor

import ink.pmc.hypervisor.config.HypervisorConfig
import ink.pmc.options.api.OptionsManager
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.currentUnixTimestamp
import ink.pmc.utils.data.listMultimapOf
import ink.pmc.utils.data.set
import ink.pmc.utils.platform.paper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction
import org.apache.commons.math3.fitting.PolynomialCurveFitter
import org.apache.commons.math3.fitting.WeightedObservedPoints
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.entity.SpawnCategory
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.math.roundToInt

class DynamicSchedulingImpl : DynamicScheduling, KoinComponent {
    private val config by lazy { get<HypervisorConfig>().dynamicScheduling }
    private val dynamicViewDistanceState = mutableMapOf<Player, DynamicViewDistanceState>()
    private var cycleJob: Job? = null

    private var isCurveCalculated = false
    private var simulateDistanceCurve: Double2IntCurve? = null
    private val defaultSpawnLimitsCurve = mutableListOf<SpawnCurve>()
    private val spawnLimitsCurve = listMultimapOf<World, SpawnCurve>()
    private val defaultTicksPerSpawnCurve = mutableListOf<SpawnCurve>()
    private val ticksPerSpawnCurve = listMultimapOf<World, SpawnCurve>()

    override val enabled: Boolean
        get() = config.enabled
    override val viewDistanceEnabled: Boolean
        get() = config.viewDistance.enabled
    override val simulateDistanceEnabled: Boolean
        get() = config.simulateDistance.enabled
    override val spawnLimitsEnabled: Boolean
        get() = config.spawnLimits.enabled
    override val ticksPerSpawnEnabled: Boolean
        get() = config.ticksPerSpawn.enabled
    override var currentSimulateDistance: Int = -1
    override val currentSpawnLimits: MutableMap<SpawnCategory, Int> = mutableMapOf()
    override val currentTicksPerSpawn: MutableMap<SpawnCategory, Int> = mutableMapOf()
    override var isRunning: Boolean = false

    override fun start() {
        check(!isRunning) { "Dynamic-scheduling cycle job already running" }
        isRunning = true
        calculateCurve()
        cycleJob = submitAsync {
            while (isRunning) {
                paper.onlinePlayers.forEach { player ->
                    val ping = player.ping
                    when {
                        ping > config.viewDistance.maximumPing
                                && !getViewDistanceLocally(player).isDisabledLocally ->
                            setViewDistanceLocally(player, DynamicViewDistanceState.DISABLED_DUE_PING)

                        ping <= config.viewDistance.maximumPing
                                && getViewDistance(player)
                                && getViewDistanceLocally(player) == DynamicViewDistanceState.DISABLED_DUE_PING ->
                            setViewDistanceLocally(player, DynamicViewDistanceState.ENABLED)
                    }
                }
                if (isCurveCalculated) {

                }
                delay(config.cyclePeriod)
            }
        }
        pluginLogger.info("Dynamic-scheduling cycle job started")
    }

    override fun stop() {
        check(isRunning) { "Dynamic-scheduling cycle job isn't running" }
        isRunning = false
        cycleJob?.cancel()
        cycleJob = null
        dynamicViewDistanceState.clear()
        pluginLogger.info("Dynamic-scheduling cycle job stopped")
    }

    override fun setViewDistance(player: Player, state: Boolean) {
        val options = runBlocking {
            OptionsManager.getOptionsOrCreate(player.uniqueId)
        }
        options.setEntry(DYNAMIC_VIEW_DISTANCE, state)
        when {
            !state && getViewDistanceLocally(player) == DynamicViewDistanceState.ENABLED ->
                setViewDistanceLocally(player, DynamicViewDistanceState.DISABLED)

            state && getViewDistanceLocally(player) == DynamicViewDistanceState.DISABLED
                -> setViewDistanceLocally(player, DynamicViewDistanceState.ENABLED)
        }
        submitAsync {
            options.save()
        }
    }

    override fun setViewDistanceLocally(player: Player, state: DynamicViewDistanceState) {
        val before = getViewDistanceLocally(player)
        dynamicViewDistanceState[player] = state
        val after = getViewDistanceLocally(player)
        if (before != after) {
            pluginLogger.info("Update ${player.name}'s dynamic view distance state: $before -> $after")
        }
    }

    override fun toggleViewDistance(player: Player) {
        if (getViewDistance(player)) {
            setViewDistance(player, false)
        } else {
            setViewDistance(player, true)
        }
    }

    override fun getViewDistance(player: Player): Boolean {
        val options = runBlocking {
            OptionsManager.getOptionsOrCreate(player.uniqueId)
        }
        val entry = options.getEntry(DYNAMIC_VIEW_DISTANCE)
        return entry?.value ?: false
    }

    override fun getViewDistanceLocally(player: Player): DynamicViewDistanceState {
        return dynamicViewDistanceState.getOrPut(player) {
            if (getViewDistance(player)) {
                DynamicViewDistanceState.ENABLED
            } else {
                DynamicViewDistanceState.DISABLED
            }
        }
    }

    override fun removeLocalViewDistanceState(player: Player) {
        dynamicViewDistanceState.remove(player)
    }

    private fun checkSample(sample: Double2IntSample) {
        check(sample.isNotEmpty()) { "Curve sample cannot be empty" }
        check(sample.size >= 2) { "Curve sample requires at least 2 points" }
    }

    private fun fitDoubleToIntCurve(sample: Double2IntSample): Double2IntCurve {
        val points = WeightedObservedPoints()
        sample.forEach { (x, y) ->
            points.add(x, y.toDouble())
        }
        val fitter = PolynomialCurveFitter.create(2)
        val coefficients = fitter.fit(points.toList())
        return Double2IntCurve(sample, PolynomialFunction(coefficients))
    }

    private fun fitSpawnStrategyCurve(strategy: SpawnStrategy): List<SpawnCurve> {
        return strategy.map { (category, sample) ->
            checkSample(sample)
            SpawnCurve(category, fitDoubleToIntCurve(sample))
        }
    }

    override fun calculateCurve() {
        val start = currentUnixTimestamp
        isCurveCalculated = false
        simulateDistanceCurve = null
        defaultSpawnLimitsCurve.clear()
        defaultTicksPerSpawnCurve.clear()
        spawnLimitsCurve.clear()
        ticksPerSpawnCurve.clear()

        val simSample = config.simulateDistance.curve
        checkSample(simSample)
        simulateDistanceCurve = fitDoubleToIntCurve(simSample)

        config.spawnLimits.default.also {
            defaultSpawnLimitsCurve.addAll(fitSpawnStrategyCurve(it))
        }

        config.spawnLimits.world.forEach { (world, strategy) ->
            val bukkitWorld = Bukkit.getWorld(world) ?: return@forEach
            spawnLimitsCurve[bukkitWorld] = fitSpawnStrategyCurve(strategy)
        }

        config.ticksPerSpawn.default.also {
            defaultTicksPerSpawnCurve.addAll(fitSpawnStrategyCurve(it))
        }

        config.ticksPerSpawn.world.forEach { (world, strategy) ->
            val bukkitWorld = Bukkit.getWorld(world) ?: return@forEach
            ticksPerSpawnCurve[bukkitWorld] = fitSpawnStrategyCurve(strategy)
        }

        pluginLogger.info("Finished function calculations in ${currentUnixTimestamp - start}ms")
        isCurveCalculated = true
    }

    private fun getHighestPoint(sample: Map<Double, Int>): Pair<Double, Int> {
        return sample.maxByOrNull { it.key }?.toPair() ?: error("Sample is empty")
    }

    override fun getSimulateDistanceWhen(millsPerSecond: Double): Int {
        check(isCurveCalculated) { "Curve not calculated" }
        val highest = getHighestPoint(config.simulateDistance.curve.toMap())
        if (millsPerSecond > highest.first) {
            return highest.second
        }
        return simulateDistanceCurve!!.function.value(millsPerSecond).roundToInt()
    }

    override fun getSimulateDistanceCurve(): PolynomialFunction {
        check(isCurveCalculated) { "Curve not calculated" }
        return simulateDistanceCurve!!.function
    }

    override fun getSpawnLimitsWhen(millsPerSecond: Double, world: World?, category: SpawnCategory): Int {
        if (world == null) {
            val curve =
                defaultSpawnLimitsCurve.firstOrNull { it.category == category } ?: return Bukkit.getSpawnLimit(category)
            val highest = curve.curve.getHighestPoint()
            if (millsPerSecond >= highest.first) {
                return highest.second
            }
            return curve.curve.function.value(millsPerSecond).roundToInt()
        } else {
            val curve =
                spawnLimitsCurve[world].firstOrNull { it.category == category } ?: return world.getSpawnLimit(category)
            val highest = curve.curve.getHighestPoint()
            if (millsPerSecond >= highest.first) {
                return highest.second
            }
            return curve.curve.function.value(millsPerSecond).roundToInt()
        }
    }

    override fun getSpawnLimitsCurve(world: World?, category: SpawnCategory): PolynomialFunction? {
        return if (world == null) {
            defaultSpawnLimitsCurve.firstOrNull { it.category == category }?.curve?.function
        } else {
            spawnLimitsCurve[world].firstOrNull { it.category == category }?.curve?.function
        }
    }

    override fun getTicksPerSpawnWhen(millsPerSecond: Double, world: World?, category: SpawnCategory): Int {
        if (world == null) {
            val curve =
                defaultTicksPerSpawnCurve
                    .firstOrNull { it.category == category } ?: return Bukkit.getTicksPerSpawns(category)
            val highest = curve.curve.getHighestPoint()
            if (millsPerSecond >= highest.first) {
                return highest.second
            }
            return curve.curve.function.value(millsPerSecond).roundToInt()
        } else {
            val curve =
                ticksPerSpawnCurve[world]
                    .firstOrNull { it.category == category } ?: return world.getTicksPerSpawns(category).toInt()
            val highest = curve.curve.getHighestPoint()
            if (millsPerSecond >= highest.first) {
                return highest.second
            }
            return curve.curve.function.value(millsPerSecond).roundToInt()
        }
    }

    override fun getTicksPerSecondCurve(world: World?, category: SpawnCategory): PolynomialFunction? {
        return if (world == null) {
            defaultTicksPerSpawnCurve.firstOrNull { it.category == category }?.curve?.function
        } else {
            ticksPerSpawnCurve[world].firstOrNull { it.category == category }?.curve?.function
        }
    }
}