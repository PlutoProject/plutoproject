package ink.pmc.framework.bridge.proxy

import ink.pmc.framework.bridge.InternalBridge
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerInfo
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.WorldInfo
import ink.pmc.framework.bridge.proxy.server.ProxyLocalServer
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.bridge.world.InternalWorld

class ProxyBridge : InternalBridge() {
    override val local: BridgeServer = ProxyLocalServer()
    override val worlds: Collection<BridgeWorld>
        get() = servers.filter { it != local }.flatMap { it.worlds }

    init {
        servers.add(local)
    }

    override fun createPlayer(info: PlayerInfo, server: InternalServer?): InternalPlayer {
        val actualServer = server ?: getServer(info.server) as InternalServer ?:
        TODO("Not yet implemented")
    }

    override fun createWorld(info: WorldInfo, server: InternalServer?): InternalWorld {
        TODO("Not yet implemented")
    }
}
