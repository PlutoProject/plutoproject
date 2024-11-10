package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.InternalBridge
import ink.pmc.framework.bridge.backend.player.BackendRemoteBackendPlayer
import ink.pmc.framework.bridge.backend.player.BackendRemoteProxyPlayer
import ink.pmc.framework.bridge.backend.server.BackendLocalServer
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerInfo
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.utils.player.uuid

class BackendBridge : InternalBridge() {
    override val local: BridgeServer = BackendLocalServer()

    override fun createPlayer(info: PlayerInfo, server: InternalServer?): InternalPlayer {
        return if (info.proxy) {
            BackendRemoteProxyPlayer(info.uniqueId.uuid, info.name)
        } else {
            val remoteServer = getInternalServer(info.server)
            BackendRemoteBackendPlayer(info.uniqueId.uuid, info.name, remoteServer, null)
        }
    }

    init {
        servers.add(local)
    }
}