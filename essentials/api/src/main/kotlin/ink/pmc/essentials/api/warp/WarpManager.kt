package ink.pmc.essentials.api.warp

import org.bukkit.Location
import org.bukkit.World

@Suppress("UNUSED")
interface WarpManager {

    val blacklistedWorlds: Collection<World>

    suspend fun getWarp(name: String): Warp?

    suspend fun getWarps(): Collection<Warp>

    suspend fun hasWarp(name: String): Boolean

    suspend fun createWarp(name: String, location: Location): Warp

    suspend fun removeWarp(name: String)

    fun isBlacklisted(world: World): Boolean

}