package ink.pmc.essentials.api.teleport

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

@Suppress("UNUSED")
interface RandomTeleportManager {

    val caches: Collection<Location>
    val maxCaches: Int
    val cacheTaskInterval: Long
    val queue: Queue<Player>
    val defaultOptions: Map<World, RandomTeleportOption>
    val blacklistedWorlds: Collection<World>

    fun pollCachedLocation(world: World): Location?

    fun findSafeLocation(world: World): Location?

    fun findSafeLocation(world: World, option: RandomTeleportOption): Location?

    fun launch(player: Player, world: World, prompt: Boolean = true)

    suspend fun launchSuspend(player: Player, world: World, prompt: Boolean = true)

    fun launch(player: Player, world: World, option: RandomTeleportOption, prompt: Boolean = true)

    suspend fun launchSuspend(player: Player, world: World, option: RandomTeleportOption, prompt: Boolean = true)

    fun isQueued(player: Player): Boolean

    fun cancel(player: Player)

    fun isBlacklisted(world: World): Boolean

    fun tick()

}