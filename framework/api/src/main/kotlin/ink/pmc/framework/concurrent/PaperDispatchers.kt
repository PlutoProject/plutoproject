package ink.pmc.framework.concurrent

import ink.pmc.framework.frameworkPaper
import ink.pmc.framework.platform.internal
import ink.pmc.framework.platform.isFolia
import ink.pmc.framework.platform.paper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Entity
import kotlin.coroutines.CoroutineContext

private val globalRegionDispatcher = object : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        paper.globalRegionScheduler.execute(frameworkPaper, block)
    }
}

val paperDispatcher: CoroutineContext = run {
    if (isFolia) return@run globalRegionDispatcher
    return@run paper.internal.asCoroutineDispatcher()
}

val Entity.dispatcher: CoroutineContext
    get() {
        if (isFolia) {
            val entity = this
            return object : CoroutineDispatcher() {
                override fun dispatch(context: CoroutineContext, block: Runnable) {
                    entity.scheduler.execute(frameworkPaper, block, {}, 0L)
                }
            }
        }
        return paperDispatcher
    }

val Chunk.dispatcher: CoroutineContext
    get() {
        if (isFolia) {
            return object : CoroutineDispatcher() {
                override fun dispatch(context: CoroutineContext, block: Runnable) {
                    val chunk = this@dispatcher
                    paper.regionScheduler.execute(frameworkPaper, chunk.world, chunk.x, chunk.z, block)
                }
            }
        }
        return paperDispatcher
    }

val Location.dispatcher
    get() = chunk.dispatcher