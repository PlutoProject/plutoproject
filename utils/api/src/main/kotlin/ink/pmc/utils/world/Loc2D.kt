package ink.pmc.utils.world

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World

@Suppress("UNUSED")
data class Loc2D(var world: World, var x: Double, var z: Double) : Cloneable {

    constructor(location: Location) : this(location.world, location.x, location.z)

    val chunk: Chunk
        get() {
            val loc = Location(world, x, 0.0, z)
            val chunk = world.getChunkAtAsync(loc) // 防止线程上下文问题

            return chunk.join()
        }

    fun add(x: Double, z: Double): Loc2D {
        this.x += x
        this.z += z
        return this
    }

    fun subtract(x: Double, z: Double): Loc2D {
        this.x -= x
        this.z -= z
        return this
    }

    override fun clone(): Loc2D {
        return Loc2D(this.world, this.x, this.z)
    }

}