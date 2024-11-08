package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.data.mutableConcurrentListOf

abstract class InternalServer : BridgeServer {
    override val players: MutableList<BridgePlayer> = mutableConcurrentListOf()
    override val worlds: MutableList<BridgeWorld> = mutableConcurrentListOf()

    override fun equals(other: Any?): Boolean {
        if (other !is BridgeServer) return false
        return other.id == id && other.type == type
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}