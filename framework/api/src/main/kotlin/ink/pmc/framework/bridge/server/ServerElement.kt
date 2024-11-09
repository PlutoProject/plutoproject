package ink.pmc.framework.bridge.server

interface ServerElement<T : ServerElement<T>> : GroupElement {
    val server: BridgeServer
    val serverState: ServerState
    val serverType: ServerType

    fun convertElement(state: ServerState = serverState, type: ServerType = serverType): T?
}