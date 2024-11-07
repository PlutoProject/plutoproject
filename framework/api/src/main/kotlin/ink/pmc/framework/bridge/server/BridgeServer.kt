package ink.pmc.framework.bridge.server

import ink.pmc.framework.bridge.player.PlayerLookup
import ink.pmc.framework.bridge.world.BridgeWorld

interface BridgeServer : PlayerLookup, Grouped {
    val id: String
    val type: ServerType
    val isOnline: Boolean

    fun getWorld(name: String): BridgeWorld?

    fun isWorldExisted(name: String): Boolean
}