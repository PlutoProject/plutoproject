package ink.pmc.framework.bridge.server

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.player.createInfo
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.ServerInfo
import ink.pmc.framework.bridge.proto.serverInfo
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.bridge.world.createInfo
import ink.pmc.framework.utils.data.mutableConcurrentSetOf

fun BridgeServer.createInfo(): ServerInfo {
    val server = this
    val localType = Bridge.local.type
    return serverInfo {
        this.id = server.id
        server.group?.id?.also { this.group = it }
        when {
            server.isLocal -> if (localType == ServerType.PROXY) proxy = true else backend = true
            server.isRemoteBackend -> backend = true
            server.isRemoteProxy -> if (localType == ServerType.PROXY) error("Unexpected") else backend = true
        }
        players.addAll(server.players.map { it.createInfo() })
        worlds.addAll(server.worlds.map { it.createInfo() })
    }
}

abstract class InternalServer : BridgeServer {
    override val players: MutableSet<BridgePlayer> = mutableConcurrentSetOf()
    override val worlds: MutableSet<BridgeWorld> = mutableConcurrentSetOf()
    abstract override var isOnline: Boolean

    fun update(info: ServerInfo) {
        
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BridgeServer) return false
        return other.id == id && other.state == state && other.type == type
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}