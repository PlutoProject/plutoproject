package ink.pmc.framework.bridge.server

class RemoteBackendServer(override val id: String, override val group: BridgeGroup?) : InternalServer() {
    override val type: ServerType = ServerType.BACKEND
    override val state: ServerState = ServerState.REMOTE
    override var isOnline: Boolean = true
}