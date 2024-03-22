package ink.pmc.common.utils.concurrent

import ink.pmc.common.utils.platform.isFolia
import ink.pmc.common.utils.platform.serverExecutor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.asCoroutineDispatcher
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Entity
import kotlin.coroutines.CoroutineContext

private val globalRegionDispatcher = object : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        globalRegionScheduler { block.run() }
    }
}

private lateinit var lateServerDispatcher: CoroutineDispatcher

private val serverDispatcher: CoroutineDispatcher
    get() {
        if (!::lateServerDispatcher.isInitialized) {
            lateServerDispatcher = serverExecutor.asCoroutineDispatcher()
        }

        return lateServerDispatcher
    }

val mainThreadDispatcher: CoroutineDispatcher
    get() {
        if (isFolia) {
            return globalRegionDispatcher
        }

        return serverDispatcher
    }

val Entity.dispatcher: CoroutineDispatcher
    get() {
        if (isFolia) {
            val entity = this

            return object : CoroutineDispatcher() {
                override fun dispatch(context: CoroutineContext, block: Runnable) {
                    entity.scheduler { block.run() }
                }
            }
        }

        return serverDispatcher
    }

private fun dispatcherForChunk(chunk: Chunk): CoroutineDispatcher {
    if (isFolia) {
        return object : CoroutineDispatcher() {
            override fun dispatch(context: CoroutineContext, block: Runnable) {
                chunk.scheduler { block.run() }
            }
        }
    }

    return serverDispatcher
}

val Chunk.dispatcher
    get() = dispatcherForChunk(this)

val Location.dispatcher
    get() = dispatcherForChunk(this.chunk)