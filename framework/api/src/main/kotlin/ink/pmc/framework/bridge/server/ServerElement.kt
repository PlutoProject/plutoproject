package ink.pmc.framework.bridge.server

interface ServerElement<T : ServerElement<T>> : GroupElement {
    val server: BridgeServer
    val serverType: ServerType
    val serverState: ServerState

    fun convertElement(state: ServerState = serverState, type: ServerType = serverType): T?
}