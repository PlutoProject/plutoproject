package ink.pmc.framework.bridge.proxy.world

import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeLocationImpl
import ink.pmc.framework.bridge.world.BridgeWorld

class ProxyRemoteBackendWorld(
    override val server: BridgeServer,
    override val name: String,
    override val alias: String?,
) : BridgeWorld {
    override val serverType: ServerType = ServerType.BACKEND
    override val serverState: ServerState = ServerState.REMOTE
    override val group: BridgeGroup? = server.group
    override lateinit var spawnPoint: BridgeLocation

    override fun getLocation(x: Double, y: Double, z: Double, yaw: Float, pitch: Float): BridgeLocation {
        return BridgeLocationImpl(server, this, x, y, z, yaw, pitch)
    }
}