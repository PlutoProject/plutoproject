package ink.pmc.utils.concurrent

import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import ink.pmc.framework.frameworkPaper
import ink.pmc.utils.platform.isFolia
import ink.pmc.utils.platform.paper
import kotlinx.coroutines.CoroutineDispatcher
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Entity
import kotlin.coroutines.CoroutineContext

private val globalRegionDispatcher = object : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        paper.globalRegionScheduler.execute(frameworkPaper, block)
    }
}

val paperDispatcher: CoroutineContext
    get() {
        if (isFolia) return globalRegionDispatcher
        return frameworkPaper.minecraftDispatcher
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