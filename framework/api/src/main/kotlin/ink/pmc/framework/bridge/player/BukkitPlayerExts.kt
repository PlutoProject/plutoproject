package ink.pmc.framework.bridge.player

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import org.bukkit.entity.Player

fun Player.toBridge(): BridgePlayer {
    return Bridge.getPlayer(uniqueId, ServerState.LOCAL, ServerType.BACKEND) ?: error("Unexpected")
}