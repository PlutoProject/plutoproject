package ink.pmc.common.utils.concurrent

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Entity
import kotlin.coroutines.CoroutineContext

val mainThreadDispatcher = object : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        globalRegionScheduler { block.run() }
    }
}

val Entity.dispatcher: CoroutineDispatcher
    get() {
        val entity = this
        return object : CoroutineDispatcher() {
            override fun dispatch(context: CoroutineContext, block: Runnable) {
                entity.scheduler { block.run() }
            }
        }
    }

private fun dispatcherForChunk(chunk: Chunk): CoroutineDispatcher {
    return object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            chunk.scheduler { block.run() }
        }
    }
}

val Chunk.dispatcher
    get() = dispatcherForChunk(this)

val Location.dispatcher
    get() = dispatcherForChunk(this.chunk)