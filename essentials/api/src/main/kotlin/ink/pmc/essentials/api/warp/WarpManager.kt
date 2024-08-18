package ink.pmc.essentials.api.warp

import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import java.util.*

@Suppress("UNUSED")
interface WarpManager {

    val blacklistedWorlds: Collection<World>
    val nameLengthLimit: Int
    val loadedWarps: Map<UUID, Warp>

    fun isLoaded(id: UUID): Boolean

    fun isLoaded(name: String): Boolean

    fun unload(id: UUID)

    fun unload(name: String)

    fun unloadAll()

    suspend fun get(id: UUID): Warp?

    suspend fun get(name: String): Warp?

    suspend fun getSpawn(id: UUID): Warp?

    suspend fun getSpawn(name: String): Warp?

    suspend fun getDefaultSpawn(): Warp?

    suspend fun getPreferredSpawn(player: OfflinePlayer): Warp?

    suspend fun setPreferredSpawn(player: OfflinePlayer, spawn: Warp)

    suspend fun list(): Collection<Warp>

    suspend fun listSpawns(): Collection<Warp>

    suspend fun has(id: UUID): Boolean

    suspend fun has(name: String): Boolean

    suspend fun setDefaultSpawn(spawn: Warp)

    suspend fun unsetDefaultSpawn(spawn: Warp)

    suspend fun create(name: String, location: Location, alias: String? = null): Warp

    suspend fun remove(id: UUID)

    suspend fun remove(name: String)

    suspend fun update(warp: Warp)

    fun isBlacklisted(world: World): Boolean

}