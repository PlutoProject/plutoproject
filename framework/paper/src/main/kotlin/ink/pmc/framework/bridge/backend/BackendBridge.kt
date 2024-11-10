package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.InternalBridge
import ink.pmc.framework.bridge.backend.server.BackendLocalServer
import ink.pmc.framework.bridge.server.BridgeServer

class BackendBridge : InternalBridge() {
    override val local: BridgeServer = BackendLocalServer()
}