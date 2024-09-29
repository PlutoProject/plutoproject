package ink.pmc.utils.bedrock

import ink.pmc.utils.platform.paper
import org.bukkit.entity.Player
import org.geysermc.floodgate.api.player.FloodgatePlayer

fun Player.asFloodgate(): FloodgatePlayer? {
    return floodgateApi?.getPlayer(uniqueId)
}

val Player.isFloodgate: Boolean
    get() = isFloodgatePlayer(uniqueId) && asFloodgate() != null

fun paperHasFloodgateSupport(): Boolean {
    return paper.pluginManager.getPlugin("floodgate") != null
}