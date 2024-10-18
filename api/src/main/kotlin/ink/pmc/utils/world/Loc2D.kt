package ink.pmc.utils.world

import org.bukkit.Location
import org.bukkit.World

@Suppress("UNUSED")
data class Loc2D(var world: World, var x: Double, var z: Double) : Cloneable {

    constructor(world: World, pos2d: Pos2D) : this(world, pos2d.x, pos2d.z)

    constructor(location: Location) : this(location.world, location.x, location.z)

    fun toPos2D(): Pos2D {
        return Pos2D(this.x, this.z)
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