package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.BridgeLocationImpl
import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.utils.data.mutableConcurrentListOf

class ProxyRemoteBackendWorld(
    override val server: BridgeServer,
    override val name: String,
    override val alias: String?,
) : BridgeWorld {
    override val players: Collection<BridgePlayer> = mutableConcurrentListOf()
    override val serverType: ServerType = ServerType.REMOTE_BACKEND
    override val group: BridgeGroup? = server.group
    override lateinit var spawnPoint: BridgeLocation

    override fun getLocation(x: Double, y: Double, z: Double, yaw: Float, pitch: Float): BridgeLocation {
        return BridgeLocationImpl(server, this, x, y, z, yaw, pitch)
    }

    override fun convertElement(type: ServerType): BridgeWorld? {
        if (type == serverType) return this
        if (type == ServerType.REMOTE_PROXY) return null
        return super.convertElement(type)
    }
}