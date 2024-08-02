package ink.pmc.essentials.api.warp

import org.bukkit.Location
import org.bukkit.World
import java.util.*

@Suppress("UNUSED")
interface WarpManager {

    val blacklistedWorlds: Collection<World>

    suspend fun get(id: UUID): Warp?

    suspend fun get(name: String): Warp?

    suspend fun list(): Collection<Warp>

    suspend fun has(name: String): Boolean

    suspend fun create(name: String, location: Location, alias: String? = null): Warp

    suspend fun remove(name: String)

    fun isBlacklisted(world: World): Boolean

}