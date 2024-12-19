package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.InternalBridge
import ink.pmc.framework.bridge.RESERVED_MASTER_ID
import ink.pmc.framework.bridge.backend.player.BackendRemoteBackendPlayer
import ink.pmc.framework.bridge.backend.player.BackendRemoteProxyPlayer
import ink.pmc.framework.bridge.backend.server.BackendLocalServer
import ink.pmc.framework.bridge.backend.server.BackendRemoteProxyServer
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerInfo
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.ServerInfo
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.bridge.throwRemoteServerNotFound
import ink.pmc.framework.bridge.warn
import ink.pmc.framework.player.uuid

class BackendBridge : InternalBridge() {
    override val local: BridgeServer = BackendLocalServer()

    override fun createRemotePlayer(info: PlayerInfo, server: InternalServer?): InternalPlayer? {
        val remoteServer = server ?: getInternalRemoteServer(info.server)
        if (remoteServer == null) {
            warn { throwRemoteServerNotFound(info.server) }
            return null
        }
        val remoteWorld = if (!remoteServer.type.isProxy) {
            getInternalRemoteWorld(remoteServer, info.world.name)
        } else {
            null
        }
        return if (info.proxy) {
            BackendRemoteProxyPlayer(info.uniqueId.uuid, info.name, remoteServer)
        } else {
            BackendRemoteBackendPlayer(info.uniqueId.uuid, info.name, remoteServer, remoteWorld)
        }
    }

    override fun createRemoteServer(info: ServerInfo): InternalServer {
        if (info.id == RESERVED_MASTER_ID) {
            val server = BackendRemoteProxyServer().apply {
                setInitialPlayers(info, this)
            }
            return server
        }
        return super.createRemoteServer(info)
    }

    init {
        servers.add(local)
    }
}