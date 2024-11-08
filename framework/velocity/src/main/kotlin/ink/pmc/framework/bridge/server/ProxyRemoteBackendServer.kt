package ink.pmc.framework.bridge.server

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.world.BridgeWorld

class ProxyRemoteBackendServer : BridgeServer {
    override val id: String
        get() = TODO("Not yet implemented")
    override val type: ServerType
        get() = TODO("Not yet implemented")
    override val isOnline: Boolean
        get() = TODO("Not yet implemented")
    override val players: Collection<BridgePlayer>
        get() = TODO("Not yet implemented")
    override val group: BridgeGroup?
        get() = TODO("Not yet implemented")

    override fun getWorld(name: String): BridgeWorld? {
        TODO("Not yet implemented")
    }

    override fun isWorldExisted(name: String): Boolean {
        TODO("Not yet implemented")
    }
}