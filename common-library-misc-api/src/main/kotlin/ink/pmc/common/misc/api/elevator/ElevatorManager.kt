package ink.pmc.common.misc.api.elevator

import ink.pmc.common.utils.world.Loc2D
import org.bukkit.Material

@Suppress("UNUSED")
interface ElevatorManager {

    val builders: Map<Material, ElevatorBuilder>

    fun registerBuilder(builder: ElevatorBuilder)

    fun getChainAt(loc: Loc2D): ElevatorChain?

}