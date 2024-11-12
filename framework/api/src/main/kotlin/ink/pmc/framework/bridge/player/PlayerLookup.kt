package ink.pmc.framework.bridge.player

import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import java.util.*

interface PlayerLookup {
    val players: Collection<BridgePlayer>
    val playerCount: Int
        get() = players.size


    fun getPlayer(name: String, state: ServerState? = null, type: ServerType? = null): BridgePlayer? {
        return players.firstOrNull {
            it.name == name
                    && it.serverState == (state ?: it.serverState)
                    && it.serverType == (type ?: it.serverType)
        }
    }

    fun getPlayer(uniqueId: UUID, state: ServerState? = null, type: ServerType? = null): BridgePlayer? {
        return players.firstOrNull {
            it.uniqueId == uniqueId
                    && it.serverState == (state ?: it.serverState)
                    && it.serverType == (type ?: it.serverType)
        }
    }

    fun getRemotePlayer(name: String): BridgePlayer? {
        return getPlayer(name, ServerState.REMOTE)
    }

    fun getRemotePlayer(uniqueId: UUID): BridgePlayer? {
        return getPlayer(uniqueId, ServerState.REMOTE)
    }

    fun isPlayerOnline(name: String): Boolean {
        return getPlayer(name) != null
    }

    fun isPlayerOnline(uniqueId: UUID): Boolean {
        return getPlayer(uniqueId) != null
    }
}