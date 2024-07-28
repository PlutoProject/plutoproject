package ink.pmc.essentials.teleport.random

import ink.pmc.essentials.api.teleport.TaskState
import ink.pmc.essentials.api.teleport.random.RandomTeleportOptions
import ink.pmc.essentials.api.teleport.random.RandomTeleportTask
import org.bukkit.World
import java.util.*

class RandomTeleportTaskImpl(
    override val id: UUID,
    override val player: World,
    override val world: World,
    override val options: RandomTeleportOptions,
    override val prompt: Boolean,
) : RandomTeleportTask {

    override var attempts: Int = 0
    override var state: TaskState = TaskState.PENDING
    override val isPending: Boolean
        get() = state == TaskState.PENDING
    override val isTicking: Boolean
        get() = state == TaskState.TICKING
    override val isFinished: Boolean
        get() = state == TaskState.FINISHED

    override suspend fun tick() {
        if (!isPending) {
            return
        }
    }

    override fun cancel() {
        state = TaskState.FINISHED
    }

}