package ink.pmc.framework.bridge.server

interface ServerLookup {
    val servers: Collection<BridgeServer>

    fun getServer(id: String): BridgeServer?

    fun isServerRegistered(id: String): Boolean
}