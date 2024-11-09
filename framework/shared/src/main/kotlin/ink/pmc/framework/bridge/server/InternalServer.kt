package ink.pmc.framework.bridge.server

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.player.toInfo
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.ServerInfo
import ink.pmc.framework.bridge.proto.serverInfo
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.bridge.world.toInfo
import ink.pmc.framework.utils.data.mutableConcurrentListOf

fun BridgeServer.toInfo(): ServerInfo {
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
        players.addAll(server.players.map { it.toInfo() })
        worlds.addAll(server.worlds.map { it.toInfo() })
    }
}

abstract class InternalServer : BridgeServer {
    override val players: MutableList<BridgePlayer> = mutableConcurrentListOf()
    override val worlds: MutableList<BridgeWorld> = mutableConcurrentListOf()
    abstract override var isOnline: Boolean

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