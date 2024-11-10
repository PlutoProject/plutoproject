package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType

class RemoteBackendWorld(
    override val server: BridgeServer,
    override val name: String,
    override val alias: String?,
) : InternalWorld() {
    override val serverType: ServerType = ServerType.BACKEND
    override val serverState: ServerState = ServerState.REMOTE
    override val group: BridgeGroup? = server.group
    override lateinit var spawnPoint: BridgeLocation

    override fun getLocation(x: Double, y: Double, z: Double, yaw: Float, pitch: Float): BridgeLocation {
        return BridgeLocationImpl(server, this, x, y, z, yaw, pitch)
    }
}