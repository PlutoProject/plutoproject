package ink.pmc.framework.bridge.server

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.InternalServer
import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.data.mutableConcurrentListOf
import ink.pmc.framework.utils.platform.proxy
import java.util.*

internal inline val localServer: ProxyLocalServer
    get() = Bridge.local as ProxyLocalServer

class ProxyLocalServer : InternalServer() {
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