package ink.pmc.essentials.api.home

import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

@Suppress("UNUSED")
interface HomeManager {

    val maxHomes: Int
    val blacklistedWorlds: Collection<World>

    suspend fun getHome(id: UUID): Home?

    suspend fun getHomes(player: OfflinePlayer): Collection<Home>

    suspend fun hasHome(player: OfflinePlayer, name: String): Boolean

    suspend fun removeHome(id: UUID)

    suspend fun createHome(owner: Player, location: Location, name: String): Home

    fun isBlacklisted(world: World): Boolean

}