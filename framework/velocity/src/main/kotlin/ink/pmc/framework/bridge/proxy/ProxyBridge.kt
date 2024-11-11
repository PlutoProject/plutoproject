package ink.pmc.framework.bridge.proxy

import ink.pmc.framework.bridge.InternalBridge
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerInfo
import ink.pmc.framework.bridge.proxy.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proxy.server.ProxyLocalServer
import ink.pmc.framework.bridge.remoteServerNotFound
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.platform.proxy
import ink.pmc.framework.utils.player.uuid

class ProxyBridge : InternalBridge() {
    override val local: BridgeServer = ProxyLocalServer()
    override val worlds: Collection<BridgeWorld>
        get() = servers.filter { it != local }.flatMap { it.worlds }

    init {
        servers.add(local)
    }

    override fun createRemotePlayer(info: PlayerInfo, server: InternalServer?): InternalPlayer {
        val actualServer = server ?: getInternalRemoteServer(info.server) ?: remoteServerNotFound(info.server)
        return ProxyRemoteBackendPlayer(proxy.getPlayer(info.uniqueId.uuid).get(), actualServer, null)
    }
}