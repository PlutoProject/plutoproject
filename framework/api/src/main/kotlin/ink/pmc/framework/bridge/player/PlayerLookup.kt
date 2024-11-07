package ink.pmc.framework.bridge.player

import java.util.*

interface PlayerLookup {
    val players: Collection<BridgePlayer>
    val playerCount: Int
        get() = players.size

    fun getPlayer(name: String): BridgePlayer? {
        return players.firstOrNull { it.name == name }
    }

    fun getPlayer(uniqueId: UUID): BridgePlayer? {
        return players.firstOrNull { it.uniqueId == uniqueId }
    }

    fun isPlayerOnline(name: String): Boolean {
        return getPlayer(name) != null
    }

    fun isPlayerOnline(uniqueId: UUID): Boolean {
        return getPlayer(uniqueId) != null
    }
}