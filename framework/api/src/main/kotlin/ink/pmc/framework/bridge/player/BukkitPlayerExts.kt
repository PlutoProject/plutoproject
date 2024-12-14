package ink.pmc.framework.bridge.player

import ink.pmc.framework.bridge.Bridge
import org.bukkit.entity.Player

fun Player.toBridge(): BridgePlayer {
    return Bridge.getPlayer(uniqueId) ?: error("Unexpected")
}