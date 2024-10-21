package ink.pmc.hypervisor

import ink.pmc.hypervisor.config.HypervisorConfig
import ink.pmc.options.api.OptionsManager
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.platform.paper
import ink.pmc.utils.time.ticks
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player
import org.bukkit.entity.SpawnCategory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DynamicSchedulingImpl : DynamicScheduling, KoinComponent {
    private val config by inject<HypervisorConfig>()
    private val dynamicViewDistanceState = mutableMapOf<Player, DynamicViewDistanceState>()
    private var cycleJob: Job? = null
    override val enabled: Boolean
        get() = config.dynamicScheduling.enabled
    override var currentSimulateDistance: Int = -1
    override val currentSpawnLimits: MutableMap<SpawnCategory, Int> = mutableMapOf()
    override val currentTicksPerSpawn: MutableMap<SpawnCategory, Int> = mutableMapOf()
    override var isRunning: Boolean = false

    override fun start() {
        check(!isRunning) { "Dynamic-scheduling cycle job already running" }
        cycleJob = submitAsync {
            paper.onlinePlayers.forEach { player ->
                val ping = player.ping
                if (ping > config.dynamicScheduling.viewDistance.maximumPing
                    && getDynamicViewDistance(player)
                    && !getDynamicViewDistanceLocally(player).isDisabledLocally
                ) {
                    setDynamicViewDistanceLocally(player, DynamicViewDistanceState.ENABLED)
                    return@forEach
                }
                if (ping <= config.dynamicScheduling.viewDistance.maximumPing
                    && getDynamicViewDistance(player)
                    && getDynamicViewDistanceLocally(player) == DynamicViewDistanceState.DISABLED_DUE_PING
                ) {
                    setDynamicViewDistanceLocally(player, DynamicViewDistanceState.ENABLED)
                    return@forEach
                }
            }
            delay(config.dynamicScheduling.cyclePeriod.ticks)
        }
        isRunning = true
        pluginLogger.info("Dynamic-scheduling cycle job started")
    }

    override fun stop() {
        check(isRunning) { "Dynamic-scheduling cycle job isn't running" }
        cycleJob = null
        isRunning = false
        pluginLogger.info("Dynamic-scheduling cycle job stopped")
    }

    override fun setDynamicViewDistance(player: Player, state: Boolean) {
        val options = runBlocking {
            OptionsManager.getOptionsOrCreate(player.uniqueId)
        }
        options.setEntry(DYNAMIC_VIEW_DISTANCE, state)
        submitAsync {
            options.save()
        }
    }

    override fun setDynamicViewDistanceLocally(player: Player, state: DynamicViewDistanceState) {
        dynamicViewDistanceState[player] = state
    }

    override fun toggleDynamicViewDistance(player: Player) {
        if (getDynamicViewDistance(player)) {
            setDynamicViewDistance(player, false)
        } else {
            setDynamicViewDistance(player, true)
        }
    }

    override fun getDynamicViewDistance(player: Player): Boolean {
        val options = runBlocking {
            OptionsManager.getOptionsOrCreate(player.uniqueId)
        }
        val entry = options.getEntry(DYNAMIC_VIEW_DISTANCE)
        return entry?.value ?: false
    }

    override fun getDynamicViewDistanceLocally(player: Player): DynamicViewDistanceState {
        return dynamicViewDistanceState.getOrPut(player) {
            if (getDynamicViewDistance(player)) DynamicViewDistanceState.ENABLED else DynamicViewDistanceState.DISABLED
        }
    }
}