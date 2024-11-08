package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.utils.data.mutableConcurrentListOf

class ProxyBridge : Bridge {
    override val self: BridgeServer = ProxyLocalServer()
    override val master: BridgeServer = self
    override val groups: MutableList<BridgeGroup> = mutableConcurrentListOf()
    override val servers: Collection<BridgeServer> = mutableConcurrentListOf()
    override val players: Collection<BridgePlayer>
        get() = servers.flatMap { it.players }
    override val playerCount: Int
        get() = players.size

    override fun getGroup(id: String): BridgeGroup? {
        return groups.firstOrNull { it.id == id }
    }

    override fun isGroupRegistered(id: String): Boolean {
        return getGroup(id) != null
    }
}