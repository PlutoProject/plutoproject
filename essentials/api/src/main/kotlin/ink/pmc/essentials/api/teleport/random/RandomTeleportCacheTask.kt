package ink.pmc.essentials.api.teleport.random

import org.bukkit.World
import java.util.*

enum class CacheTaskState {

    PENDING, TICKING, TICKING_PREPARE, FINISHED

}

interface RandomTeleportCacheTask {

    val id: UUID
    val world: World
    val options: RandomTeleportOptions
    val attempts: Int
    val state: CacheTaskState
    val isPending: Boolean
    val isTicking: Boolean
    val isFinished: Boolean

    suspend fun tick(): RandomTeleportCache?

    fun cancel()

}