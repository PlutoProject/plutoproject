package ink.pmc.essentials.api.teleport.random

import ink.pmc.essentials.api.teleport.TaskState
import org.bukkit.World
import java.util.*

interface RandomTeleportTask {

    val id: UUID
    val player: World
    val world: World
    val options: RandomTeleportOptions
    val prompt: Boolean
    val attempts: Int
    val state: TaskState
    val isPending: Boolean
    val isTicking: Boolean
    val isFinished: Boolean

    suspend fun tick()

    fun cancel()

}