package ink.pmc.essentials.api.teleport.random

import ink.pmc.utils.world.ValueChunkLoc
import org.bukkit.Chunk
import org.bukkit.World
import java.util.*

enum class CacheTaskState {

    PENDING, TICKING, TICKING_CACHE, SUCCEED, FAILED, CANCELLED

}

interface CacheTask {

    val id: UUID
    val world: World
    val options: RandomTeleportOptions
    val attempts: Int
    val pending: Collection<ValueChunkLoc>
    val cached: Collection<Chunk>
    val state: CacheTaskState
    val isPending: Boolean
    val isTicking: Boolean
    val isFinished: Boolean

    suspend fun tick(): RandomTeleportCache?

    fun cancel()

}