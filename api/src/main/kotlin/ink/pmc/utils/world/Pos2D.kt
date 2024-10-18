package ink.pmc.utils.world

import org.bukkit.Location
import org.bukkit.World

@Suppress("UNUSED")
data class Pos2D(var x: Double, var z: Double) : Cloneable {

    constructor(location: Location) : this(location.x, location.z)

    fun toLoc2D(world: World): Loc2D {
        return Loc2D(world, this)
    }

    fun add(x: Double, z: Double): Pos2D {
        this.x += x
        this.z += z
        return this
    }

    fun subtract(x: Double, z: Double): Pos2D {
        this.x -= x
        this.z -= z
        return this
    }

    override fun clone(): Pos2D {
        return Pos2D(this.x, this.z)
    }

}