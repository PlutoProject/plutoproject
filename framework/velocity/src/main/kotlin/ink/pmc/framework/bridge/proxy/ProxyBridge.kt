package ink.pmc.framework.bridge.proxy

import ink.pmc.framework.bridge.InternalBridge
import ink.pmc.framework.bridge.proxy.server.ProxyLocalServer
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeWorld

class ProxyBridge : InternalBridge() {
    override val local: BridgeServer = ProxyLocalServer()
    override val worlds: Collection<BridgeWorld>
        get() = servers.filter { it != local }.flatMap { it.worlds }

    init {
        servers.add(local)
    }
}