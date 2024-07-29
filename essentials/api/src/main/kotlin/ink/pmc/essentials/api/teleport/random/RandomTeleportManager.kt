package ink.pmc.essentials.api.teleport.random

import ink.pmc.essentials.api.teleport.ManagerState
import ink.pmc.utils.world.Pos2D
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

@Suppress("UNUSED")
interface RandomTeleportManager {

    val cacheTasks: Collection<CacheTask>
    val caches: Collection<RandomTeleportCache>
    val maxChunkCachePerTick: Int
    val maxCaches: Int
    val chunkPreserveRadius: Int
    val defaultOptions: RandomTeleportOptions
    val worldOptions: Map<World, RandomTeleportOptions>
    val enabledWorlds: Collection<World>
    val tickCount: Long
    val lastTickTime: Long
    val state: ManagerState

    fun getRandomTeleportOptions(world: World): RandomTeleportOptions

    fun getCenterLocation(world: World, options: RandomTeleportOptions? = null): Pos2D

    fun getCacheAmount(world: World): Int

    fun getCaches(world: World): Collection<RandomTeleportCache>

    fun pollCache(world: World): RandomTeleportCache?

    fun pollCache(id: UUID): RandomTeleportCache?

    suspend fun randomOnce(world: World, options: RandomTeleportOptions? = null): Location?

    suspend fun random(world: World, options: RandomTeleportOptions? = null): RandomResult

    fun submitCache(world: World, options: RandomTeleportOptions? = null): CacheTask

    fun hasCacheTask(id: UUID): Boolean

    fun launch(player: Player, world: World, options: RandomTeleportOptions? = null, prompt: Boolean = true)

    suspend fun launchSuspend(
        player: Player,
        world: World,
        options: RandomTeleportOptions? = null,
        prompt: Boolean = true
    )

    fun isEnabled(world: World): Boolean

    suspend fun tick()

}