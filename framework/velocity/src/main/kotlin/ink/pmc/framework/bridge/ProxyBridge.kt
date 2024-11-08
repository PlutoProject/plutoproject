package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ProxyLocalServer
import ink.pmc.framework.utils.data.mutableConcurrentListOf
import org.koin.java.KoinJavaComponent.getKoin

internal inline val proxyBridge: ProxyBridge
    get() = getKoin().get<Bridge>() as ProxyBridge

class ProxyBridge : Bridge {
    override val local: BridgeServer = ProxyLocalServer()
    override val master: BridgeServer = local
    override val groups: MutableList<BridgeGroup> = mutableConcurrentListOf()
    override val servers: MutableList<BridgeServer> = mutableConcurrentListOf()
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