package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.data.mutableConcurrentListOf

abstract class InternalServer : BridgeServer {
    override val players: MutableList<BridgePlayer> = mutableConcurrentListOf()
    override val worlds: MutableList<BridgeWorld> = mutableConcurrentListOf()
}