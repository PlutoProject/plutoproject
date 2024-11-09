package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerInfo
import ink.pmc.framework.bridge.proto.playerInfo
import ink.pmc.framework.bridge.world.BridgeWorld

fun BridgePlayer.toInfo(): PlayerInfo {
    val player = this
    return playerInfo {
        server = player.server.id
        uniqueId = player.uniqueId.toString()
        name = player.name
        player.world?.also { world = it.toInfo() }
    }
}

abstract class InternalPlayer : BridgePlayer {
    abstract override var world: BridgeWorld?
    abstract override var isOnline: Boolean

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
