package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.LocationInfo
import ink.pmc.framework.bridge.proto.locationInfo
import ink.pmc.framework.bridge.server.BridgeServer

fun LocationInfo.toImpl(server: BridgeServer, world: BridgeWorld): BridgeLocation {
    return BridgeLocationImpl(server, world, x, y, z, yaw, pitch)
}

fun BridgeLocation.toInfo(): LocationInfo {
    val loc = this
    return locationInfo {
        server = loc.server.id
        world = loc.world.name
        x = loc.x
        y = loc.y
        z = loc.z
        yaw = loc.yaw
        pitch = loc.pitch
    }
}

data class BridgeLocationImpl(
    override val server: BridgeServer,
    override val world: BridgeWorld,
    override val x: Double,
    override val y: Double,
    override val z: Double,
    override val yaw: Float = 0.0F,
    override val pitch: Float = 0.0F
) : BridgeLocation