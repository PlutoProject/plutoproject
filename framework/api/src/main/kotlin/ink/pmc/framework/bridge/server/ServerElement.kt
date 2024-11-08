package ink.pmc.framework.bridge.server

interface ServerElement<T : ServerElement<T>> : Grouped {
    val server: BridgeServer
    val serverType: ServerType
    val isLocal: Boolean get() = serverType == ServerType.LOCAL
    val isRemoteProxy: Boolean get() = serverType == ServerType.REMOTE_PROXY
    val isRemoteBackend: Boolean get() = serverType == ServerType.REMOTE_BACKEND

    fun convertElement(type: ServerType): T?
}