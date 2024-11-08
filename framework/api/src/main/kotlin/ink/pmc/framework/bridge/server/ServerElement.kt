package ink.pmc.framework.bridge.server

interface ServerElement : Grouped {
    val server: BridgeServer
    val serverType: ServerType
    val isLocal: Boolean get() = serverType == ServerType.LOCAL
    val isRemoteProxy: Boolean get() = serverType == ServerType.REMOTE_PROXY
    val isRemoteBackend: Boolean get() = serverType == ServerType.REMOTE_BACKEND

    fun <T : ServerElement> convertElement(type: ServerType): T?
}