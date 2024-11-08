package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.LocationInfo
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld

fun LocationInfo.toImpl(server: BridgeServer, world: BridgeWorld): BridgeLocation {
    return BridgeLocationImpl(server, world, x, y, z, yaw, pitch)
}

class BridgeLocationImpl(
    override val server: BridgeServer,
    override val world: BridgeWorld,
    override val x: Double,
    override val y: Double,
    override val z: Double,
    override val yaw: Float = 0.0F,
    override val pitch: Float = 0.0F
) : BridgeLocation {
    override val serverType: ServerType = server.type
    override val group: BridgeGroup? = server.group

    override fun convertElement(type: ServerType): BridgeLocation? {
        error("Unsupported")
    }
}