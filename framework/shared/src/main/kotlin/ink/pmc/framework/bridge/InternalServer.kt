package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.ServerInfo
import ink.pmc.framework.bridge.proto.serverInfo
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerType.*
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.data.mutableConcurrentListOf

fun BridgeServer.toInfo(isLocalProxy: Boolean): ServerInfo {
    val server = this
    return serverInfo {
        this.id = server.id
        server.group?.id?.also { this.group = it }
        when (server.type) {
            LOCAL -> if (isLocalProxy) proxy = true else backend = true
            REMOTE_PROXY -> if (isLocalProxy) error("Unexpected") else proxy = true
            REMOTE_BACKEND -> backend = true
        }
        players.addAll(server.players.map { it.toInfo() })
        worlds.addAll(server.worlds.map { it.toInfo() })
    }
}

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