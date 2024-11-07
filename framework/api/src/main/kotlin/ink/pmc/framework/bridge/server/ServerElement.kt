package ink.pmc.framework.bridge.server

interface ServerElement {
    val server: BridgeServer
    val serverType: ServerElementType
    val isLocal: Boolean get() = serverType == ServerElementType.LOCAL
    val isRemoteProxy: Boolean get() = serverType == ServerElementType.REMOTE_PROXY
    val isRemoteBackend: Boolean get() = serverType == ServerElementType.REMOTE_BACKEND

    fun convertServer(type: ServerElementType): ServerElement?
}