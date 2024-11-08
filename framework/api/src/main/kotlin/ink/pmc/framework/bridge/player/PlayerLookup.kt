package ink.pmc.framework.bridge.player

import ink.pmc.framework.bridge.server.ServerType
import java.util.*

interface PlayerLookup {
    val players: Collection<BridgePlayer>
    val playerCount: Int
        get() = players.size

    fun getPlayer(name: String, type: ServerType? = null): BridgePlayer? {
        return if (type == null) {
            players.firstOrNull { it.name == name }
        } else {
            players.firstOrNull { it.name == name && it.serverType == type }
        }
    }

    fun getPlayer(uniqueId: UUID, type: ServerType? = null): BridgePlayer? {
        return if (type == null) {
            players.firstOrNull { it.uniqueId == uniqueId }
        } else {
            players.firstOrNull { it.uniqueId == uniqueId && it.serverType == type }
        }
    }

    fun getRemotePlayer(name: String): BridgePlayer? {
        return players.firstOrNull { it.name == name && !it.isLocal }
    }

    fun getRemotePlayer(uniqueId: UUID): BridgePlayer? {
        return players.firstOrNull { it.uniqueId == uniqueId && !it.isLocal }
    }

    fun isPlayerOnline(name: String): Boolean {
        return getPlayer(name) != null
    }

    fun isPlayerOnline(uniqueId: UUID): Boolean {
        return getPlayer(uniqueId) != null
    }
}