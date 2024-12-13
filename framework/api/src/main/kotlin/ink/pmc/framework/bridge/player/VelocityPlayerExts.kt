package ink.pmc.framework.bridge.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType

fun Player.toBridge(): BridgePlayer {
    return Bridge.getPlayer(uniqueId, ServerState.LOCAL, ServerType.PROXY) ?: error("Unexpected")
}