package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.InternalBridge
import ink.pmc.framework.bridge.backend.server.BackendLocalServer
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.bridge.world.InternalWorld

class BackendBridge : InternalBridge() {
    override val local: BridgeServer = BackendLocalServer()

    override fun createPlayer(info: BridgeRpcOuterClass.PlayerInfo, server: InternalServer?): InternalPlayer {
    }

    override fun createWorld(info: BridgeRpcOuterClass.WorldInfo, server: InternalServer?): InternalWorld {
        TODO("Not yet implemented")
    }
}