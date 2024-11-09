package ink.pmc.framework.bridge.server

interface ServerElement<T : ServerElement<T>> : Grouped {
    val server: BridgeServer
    val serverType: ServerType
    val serverState: ServerState
    val isLocal: Boolean get() = serverState == ServerState.LOCAL
    val isRemoteBackend: Boolean get() = serverState == ServerState.REMOTE && serverType == ServerType.BACKEND
    val isRemoteProxy: Boolean get() = serverState == ServerState.REMOTE && serverType == ServerType.PROXY

    fun convertElement(type: ServerType): T?
}