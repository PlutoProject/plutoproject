package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.data.mutableConcurrentListOf
import ink.pmc.framework.utils.platform.proxy
import java.util.*

internal val localServer: ProxyLocalServer
    get() = Bridge.local as ProxyLocalServer

class ProxyLocalServer : BridgeServer {
    override val id: String = "_master"
    override val type: ServerType = ServerType.LOCAL
    override val isOnline: Boolean = true
    override val players: MutableList<BridgePlayer> = mutableConcurrentListOf()
    override val playerCount: Int
        get() = proxy.playerCount
    override val group: BridgeGroup? = null

    override fun getWorld(name: String): BridgeWorld? {
        error("Unsupported")
    }

    override fun isWorldExisted(name: String): Boolean {
        error("Unsupported")
    }

    override fun isPlayerOnline(name: String): Boolean {
        return proxy.getPlayer(name).isPresent
    }

    override fun isPlayerOnline(uniqueId: UUID): Boolean {
        return proxy.getPlayer(uniqueId).isPresent
    }
}