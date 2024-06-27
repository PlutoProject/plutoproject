package ink.pmc.utils.world

import org.bukkit.Location

fun toRawLocation(location: Location): Location {
    val rawLocation = location.toBlockLocation()

    rawLocation.yaw = 0F
    rawLocation.pitch = 0F

    return rawLocation
}

@Suppress("UNUSED")
val Location.loc2D: Loc2D
    get() = Loc2D(this)

@Suppress("UNUSED")
val Location.rawLoc2D: Loc2D
    get() = Loc2D(this.rawLocation)

val Location.rawLocation: Location
    get() = toRawLocation(this)

fun minLocation(a: Location, b: Location): Location {
    return Location(
        a.world,
        minOf(a.x, b.x),
        minOf(a.y, b.y),
        minOf(a.z, b.z)
    )
}

fun maxLocation(a: Location, b: Location): Location {
    return Location(
        a.world,
        maxOf(a.x, b.x),
        maxOf(a.y, b.y),
        maxOf(a.z, b.z)
    )
}