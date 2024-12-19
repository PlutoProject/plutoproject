package ink.pmc.framework.bridge.proxy

import ink.pmc.framework.bridge.InternalBridge
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerInfo
import ink.pmc.framework.bridge.proxy.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proxy.server.ProxyLocalServer
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.bridge.throwRemoteServerNotFound
import ink.pmc.framework.bridge.warn
import ink.pmc.framework.platform.proxy
import ink.pmc.framework.player.uuid

class ProxyBridge : InternalBridge() {
    override val local: BridgeServer = ProxyLocalServer()

    init {
        servers.add(local)
    }

    override fun createRemotePlayer(info: PlayerInfo, server: InternalServer?): InternalPlayer? {
        val actualServer = server ?: getInternalRemoteServer(info.server)
        if (actualServer == null) {
            warn { throwRemoteServerNotFound(info.server) }
            return null
        }
        val remoteWorld = internalBridge.getInternalRemoteWorld(actualServer, info.world.name)
        return ProxyRemoteBackendPlayer(proxy.getPlayer(info.uniqueId.uuid).get(), actualServer, remoteWorld)
    }
}