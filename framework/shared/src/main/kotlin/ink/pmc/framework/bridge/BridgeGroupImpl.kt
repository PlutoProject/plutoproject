package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.server.BridgeGroup

class BridgeGroupImpl(override val id: String) : BridgeGroup {
    override val players: Collection<BridgePlayer>
        get() = Bridge.players.filter { it.group == this }

    override fun equals(other: Any?): Boolean {
        if (other !is BridgeGroup) return false
        return other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}