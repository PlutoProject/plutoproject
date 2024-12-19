package ink.pmc.framework.world

import kotlinx.coroutines.future.await
import org.bukkit.Chunk
import org.bukkit.World
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
@JvmInline
value class ValueVec2(private val value: Long) {
    constructor(x: Int, y: Int) : this(x.toLong() shl 32 or y.toLong())

    val x: Int
        get() = (value ushr 32).toInt()
    val y: Int
        get() = value.toInt()

    fun isLoaded(world: World): Boolean {
        return world.isChunkLoaded(x, y)
    }

    fun getChunk(world: World): Chunk {
        return world.getChunkAt(x, y)
    }

    fun getChunkAsync(world: World): CompletableFuture<Chunk> {
        return world.getChunkAtAsync(x, y)
    }

    suspend fun getChunkSuspend(world: World): Chunk {
        return getChunkAsync(world).await()
    }
}