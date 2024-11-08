package ink.pmc.framework.bridge.server

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.data.mutableConcurrentListOf

class ProxyRemoteBackendServer(override val id: String, override val group: BridgeGroup?) : BridgeServer {
    override val type: ServerType = ServerType.REMOTE_BACKEND
    override val worlds: MutableList<BridgeWorld> = mutableConcurrentListOf()
    override var isOnline: Boolean = true
    override val players: MutableList<BridgePlayer> = mutableConcurrentListOf()

    override fun getWorld(name: String): BridgeWorld? {
        TODO("Not yet implemented")
    }

    override fun isWorldExisted(name: String): Boolean {
        TODO("Not yet implemented")
    }
}