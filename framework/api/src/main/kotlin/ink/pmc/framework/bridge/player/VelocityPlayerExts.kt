package ink.pmc.framework.bridge.player

import com.velocitypowered.api.proxy.Player
import ink.pmc.framework.bridge.Bridge

fun Player.toBridge(): BridgePlayer {
    return Bridge.getPlayer(uniqueId) ?: error("Unexpected")
}