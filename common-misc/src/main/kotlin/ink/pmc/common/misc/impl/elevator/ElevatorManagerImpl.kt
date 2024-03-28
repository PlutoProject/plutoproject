package ink.pmc.common.misc.impl.elevator

import ink.pmc.common.misc.api.elevator.ElevatorBuilder
import ink.pmc.common.misc.api.elevator.ElevatorChain
import ink.pmc.common.misc.api.elevator.ElevatorManager
import ink.pmc.common.utils.world.rawLocation
import org.bukkit.Location
import org.bukkit.Material

class ElevatorManagerImpl : ElevatorManager {

    private val materialToBuilderMap = mutableMapOf<Material, ElevatorBuilder>()

    override val builders: Map<Material, ElevatorBuilder>
        get() = materialToBuilderMap

    override fun registerBuilder(builder: ElevatorBuilder) {
        materialToBuilderMap[builder.type] = builder
    }

    override suspend fun getChainAt(loc: Location): ElevatorChain? {
        val offsetLoc = loc.clone().subtract(0.0, 1.0, 0.0).rawLocation

        val type = offsetLoc.block.type

        if (!materialToBuilderMap.containsKey(type)) {
            return null
        }

        val builder = materialToBuilderMap[type]!!

        if (builder.teleportLocations(loc).size < 2) {
            return null
        }

        val chain = ElevatorChainImpl(builder.findLocations(loc), builder.teleportLocations(loc))
        return chain
    }

}