package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer

abstract class InternalPlayer : BridgePlayer {
    override fun equals(other: Any?): Boolean {
        if (other !is BridgePlayer) return false
        return other.uniqueId == uniqueId && other.serverType == serverType
    }

    override fun hashCode(): Int {
        var result = uniqueId.hashCode()
        result = 31 * result + serverType.hashCode()
        return result
    }
}
