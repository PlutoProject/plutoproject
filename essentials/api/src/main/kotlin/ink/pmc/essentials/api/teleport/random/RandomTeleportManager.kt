package ink.pmc.essentials.api.teleport.random

import ink.pmc.essentials.api.teleport.ManagerState
import ink.pmc.utils.world.Pos2D
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

@Suppress("UNUSED")
interface RandomTeleportManager {

    val cacheTasks: Collection<RandomTeleportCacheTask>
    val teleportQueue: Queue<RandomTeleportTask>
    val caches: Collection<RandomTeleportCache>
    val maxCaches: Int
    val defaultOptions: RandomTeleportOptions
    val worldOptions: Map<World, RandomTeleportOptions>
    val blacklistedWorlds: Collection<World>
    val tickCount: Long
    val lastTickTime: Long
    val state: ManagerState

    fun getRandomTeleportOptions(world: World): RandomTeleportOptions

    fun getCenterLocation(world: World): Pos2D

    fun pollCache(world: World): Location?

    fun searchSafeLocation(world: World, options: RandomTeleportOptions? = null): Location?

    fun launch(player: Player, world: World, options: RandomTeleportOptions? = null, prompt: Boolean = true): RandomTeleportTask

    suspend fun launchSuspend(
        player: Player,
        world: World,
        options: RandomTeleportOptions? = null,
        prompt: Boolean = true
    ): RandomTeleportTask

    fun cancel(id: UUID)

    fun cancel(player: Player)

    fun isBlacklisted(world: World): Boolean

    suspend fun tick()

}