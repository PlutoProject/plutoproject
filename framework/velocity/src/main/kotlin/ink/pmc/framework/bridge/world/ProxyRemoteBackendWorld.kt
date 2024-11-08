package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerElement
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
        TODO("Not yet implemented")
    }

    override fun <T : ServerElement> convertElement(type: ServerType): T? {
        TODO("Not yet implemented")
    }
}