package ink.pmc.framework.bridge.player

import java.util.*

interface PlayerLookup {
    val players: Collection<BridgePlayer>
    val playerCount: Int

    fun getPlayer(name: String): BridgePlayer?

    fun getPlayer(uniqueId: UUID): BridgePlayer?

    fun isPlayerOnline(name: String): Boolean

    fun isPlayerOnline(uniqueId: UUID): Boolean
}