package ink.pmc.essentials.teleport.random

import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.*
import ink.pmc.essentials.plugin
import ink.pmc.utils.world.ValueChunkLoc
import kotlinx.coroutines.future.await
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.*

class CacheTaskImpl(
    override val world: World,
    override val options: RandomTeleportOptions,
) : CacheTask, KoinComponent {

    private var manager = get<RandomTeleportManager>() as RandomTeleportManagerImpl
    private val teleport by inject<TeleportManager>()
    private var location: Location? = null
    private var pendingCount = -1

    override val id: UUID = UUID.randomUUID()
    override var attempts: Int = 0
    override val pending: MutableList<ValueChunkLoc> = mutableListOf()
    override val cached: MutableList<Chunk> = mutableListOf()
    override var state: CacheTaskState = CacheTaskState.PENDING
    override val isPending: Boolean
        get() = state == CacheTaskState.PENDING
    override val isTicking: Boolean
        get() = state == CacheTaskState.TICKING || state == CacheTaskState.TICKING_CACHE
    override val isFinished: Boolean
        get() = state == CacheTaskState.SUCCEED || state == CacheTaskState.FAILED || state == CacheTaskState.CANCELLED

    override suspend fun tick(): RandomTeleportCache? {
        if (isFinished) {
            return null
        }

        if (location == null) {
            state = CacheTaskState.TICKING
            if (attempts < options.maxAttempts) {
                attempts++
                val random = manager.randomOnce(world, options)
                if (random != null) {
                    location = random
                }
                return null
            }
            state = CacheTaskState.FAILED
            return null
        }

        if (pending.isEmpty() && state != CacheTaskState.TICKING_CACHE) {
            pending.addAll(teleport.getRequiredChunks(location!!, manager.chunkPreserveRadius))
            pendingCount = pending.size
        }

        state = CacheTaskState.TICKING_CACHE

        if (cached.size == pendingCount) {
            state = CacheTaskState.SUCCEED
            return RandomTeleportCache(id, world, location!!.chunk, cached, attempts, location!!, options)
        }

        if (manager.currentTickCaches < manager.maxChunkCachePerTick) {
            val loc = pending.removeFirst() ?: return null
            val chunk = world.getChunkAtAsyncUrgently(loc.x, loc.y).await()
            cached.add(chunk)
            chunk.addPluginChunkTicket(plugin)
            manager.currentTickCaches++
            return null
        }

        return null
    }

    override fun cancel() {
        state = CacheTaskState.CANCELLED
    }

}