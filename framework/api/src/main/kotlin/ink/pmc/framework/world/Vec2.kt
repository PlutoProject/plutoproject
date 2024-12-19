package ink.pmc.framework.world

import org.bukkit.Location
import org.bukkit.World

@Suppress("UNUSED")
data class Vec2(var x: Double, var z: Double) : Cloneable {
    constructor(location: Location) : this(location.x, location.z)

    fun toLoc2(world: World): Loc2 {
        return Loc2(world, this)
    }

    fun add(x: Double, z: Double): Vec2 {
        this.x += x
        this.z += z
        return this
    }

    fun subtract(x: Double, z: Double): Vec2 {
        this.x -= x
        this.z -= z
        return this
    }

    override fun clone(): Vec2 {
        return Vec2(this.x, this.z)
    }
}