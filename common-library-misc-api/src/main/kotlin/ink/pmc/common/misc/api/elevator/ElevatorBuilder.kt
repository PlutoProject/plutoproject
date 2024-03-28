package ink.pmc.common.misc.api.elevator

import org.bukkit.Location
import org.bukkit.Material

@Suppress("UNUSED")
interface ElevatorBuilder {

    val type: Material
    val permission: String?

    fun findLocations(start: Location): List<Location>

}