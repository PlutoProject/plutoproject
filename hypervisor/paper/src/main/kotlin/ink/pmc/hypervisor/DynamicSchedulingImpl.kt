package ink.pmc.hypervisor

import ink.pmc.hypervisor.config.HypervisorConfig
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DynamicSchedulingImpl : DynamicScheduling, KoinComponent {
    private val config by inject<HypervisorConfig>()
    override val enabled: Boolean
        get() = config.dynamicScheduling.enabled

    override fun setDynamicViewDistanceState(player: Player, state: Boolean) {
        TODO("Not yet implemented")
    }

    override fun toggleDynamicViewDistance(player: Player) {
        TODO("Not yet implemented")
    }

    override fun isDynamicViewDistanceEnabled(player: Player): Boolean {
        TODO("Not yet implemented")
    }
}