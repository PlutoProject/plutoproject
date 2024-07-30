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

    suspend fun get(id: UUID): Home?

    suspend fun list(player: OfflinePlayer): Collection<Home>

    suspend fun has(player: OfflinePlayer, name: String): Boolean

    suspend fun remove(id: UUID)

    suspend fun remove(player: OfflinePlayer, name: String)

    suspend fun create(owner: Player, name: String, location: Location): Home

    suspend fun update(home: Home)

    fun isBlacklisted(world: World): Boolean

}