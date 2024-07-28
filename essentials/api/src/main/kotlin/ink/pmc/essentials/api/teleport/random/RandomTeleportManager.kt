package ink.pmc.essentials.api.teleport.random

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

@Suppress("UNUSED")
interface RandomTeleportManager {

    val caches: Collection<Location>
    val maxCaches: Int
    val cacheTaskInterval: Long
    val defaultOptions: Map<World, RandomTeleportOptions>
    val blacklistedWorlds: Collection<World>

    fun getRandomTeleportOptions(world: World): RandomTeleportOptions

    fun pollCache(world: World): Location?

    fun searchSafeLocation(world: World, options: RandomTeleportOptions? = null): Location?

    fun launch(player: Player, world: World, options: RandomTeleportOptions? = null, prompt: Boolean = true)

    suspend fun launchSuspend(
        player: Player,
        world: World,
        options: RandomTeleportOptions? = null,
        prompt: Boolean = true
    )

    fun isQueued(player: Player): Boolean

    fun cancel(player: Player)

    fun isBlacklisted(world: World): Boolean

    fun tick()

}