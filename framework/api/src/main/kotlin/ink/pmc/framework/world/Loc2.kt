package ink.pmc.framework.world

import org.bukkit.Location
import org.bukkit.World

@Suppress("UNUSED")
data class Loc2(var world: World, var x: Double, var z: Double) : Cloneable {
    constructor(world: World, vec2: Vec2) : this(world, vec2.x, vec2.z)

    constructor(location: Location) : this(location.world, location.x, location.z)

    fun toVec2(): Vec2 {
        return Vec2(this.x, this.z)
    }

    fun add(x: Double, z: Double): Loc2 {
        this.x += x
        this.z += z
        return this
    }

    fun subtract(x: Double, z: Double): Loc2 {
        this.x -= x
        this.z -= z
        return this
    }

    override fun clone(): Loc2 {
        return Loc2(this.world, this.x, this.z)
    }
}