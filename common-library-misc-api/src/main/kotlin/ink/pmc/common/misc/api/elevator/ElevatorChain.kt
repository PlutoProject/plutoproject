package ink.pmc.common.misc.api.elevator

import org.bukkit.Location
import org.bukkit.entity.Player

@Suppress("UNUSED")
interface ElevatorChain {

    val floors: List<Location>

    fun up(player: Player)

    fun down(player: Player)

    fun go(player: Player, floor: Int)

}