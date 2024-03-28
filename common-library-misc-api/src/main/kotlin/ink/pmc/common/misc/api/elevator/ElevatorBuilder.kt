package ink.pmc.common.misc.api.elevator

import org.bukkit.Location
import org.bukkit.Material

@Suppress("UNUSED")
interface ElevatorBuilder {

    val startPoint: Location
    val type: Material
    val permission: String?

    suspend fun findLocations(): List<Location>

    suspend fun teleportLocations(): List<Location>

}