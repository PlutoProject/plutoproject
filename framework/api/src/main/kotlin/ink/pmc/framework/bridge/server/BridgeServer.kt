package ink.pmc.framework.bridge.server

import ink.pmc.framework.bridge.player.PlayerLookup
import ink.pmc.framework.bridge.world.BridgeWorld

interface BridgeServer : PlayerLookup {
    val id: String
    val isOnline: String

    fun getWorld(name: String): BridgeWorld?

    fun isWorldExisted(name: String): Boolean
}