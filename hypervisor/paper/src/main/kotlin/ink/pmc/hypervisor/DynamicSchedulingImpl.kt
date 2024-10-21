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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DynamicSchedulingImpl : DynamicScheduling, KoinComponent {
    private val config by inject<HypervisorConfig>()
    private val localDynamicViewDistanceState = mutableMapOf<Player, Boolean>()
    private var cycleJob: Job? = null
    override val enabled: Boolean
        get() = config.dynamicScheduling.enabled
    override var state: SchedulingState = SchedulingState(-1, mapOf(), mapOf())
    override var isRunning: Boolean = false

    override fun start() {
        check(!isRunning) { "Dynamic-scheduling cycle job already running" }
        cycleJob = submitAsync {
            paper.onlinePlayers.forEach { player ->
                val ping = player.ping
                if (ping > config.dynamicScheduling.viewDistance.maximumPing
                    && isDynamicViewDistanceEnabled(player)
                    && isDynamicViewDistanceEnabledLocally(player)
                ) {
                    setDynamicViewDistanceStateLocally(player, false)
                    return@forEach
                }
                if (ping < config.dynamicScheduling.viewDistance.maximumPing
                    && isDynamicViewDistanceEnabled(player)
                    && !isDynamicViewDistanceEnabledLocally(player)
                ) {
                    setDynamicViewDistanceStateLocally(player, true)
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

    override fun setDynamicViewDistanceState(player: Player, state: Boolean) {
        val options = runBlocking {
            OptionsManager.getOptionsOrCreate(player.uniqueId)
        }
        options.setEntry(DYNAMIC_VIEW_DISTANCE, state)
        submitAsync {
            options.save()
        }
    }

    override fun setDynamicViewDistanceStateLocally(player: Player, state: Boolean) {
        localDynamicViewDistanceState[player] = state
    }

    override fun toggleDynamicViewDistance(player: Player) {
        if (isDynamicViewDistanceEnabled(player)) {
            setDynamicViewDistanceState(player, false)
        } else {
            setDynamicViewDistanceState(player, true)
        }
    }

    override fun isDynamicViewDistanceEnabled(player: Player): Boolean {
        val options = runBlocking {
            OptionsManager.getOptionsOrCreate(player.uniqueId)
        }
        val entry = options.getEntry(DYNAMIC_VIEW_DISTANCE)
        return entry?.value ?: false
    }

    override fun isDynamicViewDistanceEnabledLocally(player: Player): Boolean {
        return localDynamicViewDistanceState.getOrPut(player) { isDynamicViewDistanceEnabled(player) }
    }
}