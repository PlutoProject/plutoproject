package ink.pmc.utils.world

import kotlinx.coroutines.future.await
import org.bukkit.Chunk
import org.bukkit.World
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
class ChunkLoc(var x: Int, var y: Int) {

    fun add(x: Int, y: Int): ChunkLoc {
        this.x += x
        this.y += y
        return this
    }

    fun subtract(x: Int, y: Int): ChunkLoc {
        this.x -= x
        this.y -= y
        return this
    }

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