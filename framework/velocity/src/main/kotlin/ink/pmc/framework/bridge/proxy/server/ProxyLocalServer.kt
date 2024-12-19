package ink.pmc.framework.bridge.proxy.server

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.RESERVED_MASTER_ID
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.platform.proxy
import java.util.*

internal inline val localServer: ProxyLocalServer
    get() = Bridge.local as ProxyLocalServer

class ProxyLocalServer : InternalServer() {
    override val group: BridgeGroup? = null
    override val id: String = RESERVED_MASTER_ID
    override val type: ServerType = ServerType.PROXY
    override val state: ServerState = ServerState.LOCAL
    override val worlds: MutableSet<BridgeWorld>
        get() = error("Unsupported")
    override var isOnline: Boolean = true
        set(_) = error("Unsupported")

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