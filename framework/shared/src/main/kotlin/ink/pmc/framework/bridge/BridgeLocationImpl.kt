package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerElement
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld

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

    override fun <T : ServerElement> convertElement(type: ServerType): T? {
        TODO("Not yet implemented")
    }
}