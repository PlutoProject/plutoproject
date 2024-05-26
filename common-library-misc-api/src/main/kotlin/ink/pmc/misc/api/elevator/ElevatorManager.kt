package ink.pmc.misc.api.elevator

import org.bukkit.Location
import org.bukkit.Material

@Suppress("UNUSED")
interface ElevatorManager {

    val builders: Map<Material, ElevatorBuilder>

    fun registerBuilder(builder: ElevatorBuilder)

    suspend fun getChainAt(loc: Location): ElevatorChain?

}