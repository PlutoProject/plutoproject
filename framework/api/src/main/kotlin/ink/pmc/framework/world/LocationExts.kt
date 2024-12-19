package ink.pmc.framework.world

import org.bukkit.Location

fun Location.eraseAngle(): Location {
    val rawLocation = clone().toBlockLocation()
    rawLocation.yaw = 0F
    rawLocation.pitch = 0F
    return rawLocation
}

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

fun Location.blockEquals(other: Location): Boolean {
    return blockX == other.blockX && blockY == other.blockY && blockZ == other.blockZ
}