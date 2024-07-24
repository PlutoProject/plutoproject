package ink.pmc.essentials.api.back

import com.google.common.collect.Multimap
import org.bukkit.Location
import org.bukkit.entity.Player

@Suppress("UNUSED")
interface BackManager {

    val maxStoredBacks: Int
    val storedBacks: Multimap<Player, Location>

    fun getStoredLocations(player: Player): Collection<Location>

    fun back(player: Player, prompt: Boolean = true): Location

    fun store(player: Player, location: Location)

    fun discard(player: Player)

    fun poll(player: Player): Location

}