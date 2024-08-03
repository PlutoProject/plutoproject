package ink.pmc.essentials.api.back

import org.bukkit.Location
import org.bukkit.entity.Player

@Suppress("UNUSED")
interface BackManager {

    val maxLocations: Int
    val previousLocations: Map<Player, Location>

    fun has(player: Player): Boolean

    fun get(player: Player): Location?

    fun back(player: Player)

    suspend fun backSuspend(player: Player)

    suspend fun store(player: Player, location: Location)

    fun discard(player: Player)

}