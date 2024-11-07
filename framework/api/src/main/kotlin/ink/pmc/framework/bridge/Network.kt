package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.PlayerLookup
import ink.pmc.framework.bridge.server.BridgeServer

interface Network : PlayerLookup {
    val master: BridgeServer
    val servers: Collection<BridgeServer>

    fun getServer(id: String): BridgeServer?

    fun isServerRegistered(id: String): Boolean
}