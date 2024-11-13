package ink.pmc.framework.bridge.backend.server

import ink.pmc.framework.bridge.RESERVED_MASTER_ID
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeWorld

class BackendRemoteProxyServer : InternalServer() {
    override val id: String = RESERVED_MASTER_ID
    override val group: BridgeGroup? = null
    override val state: ServerState = ServerState.REMOTE
    override val type: ServerType = ServerType.PROXY
    override val worlds: MutableSet<BridgeWorld>
        get() = error("Unsupported")
    override var isOnline: Boolean = true
}