package ink.pmc.utils.bedrock

import com.velocitypowered.api.proxy.Player
import ink.pmc.utils.platform.proxy
import org.geysermc.floodgate.api.player.FloodgatePlayer

fun Player.asFloodgate(): FloodgatePlayer? {
    return floodgateApi?.getPlayer(uniqueId)
}

val Player.isFloodgate: Boolean
    get() = asFloodgate() != null

fun velocityHasFloodgateSupport(): Boolean {
    return proxy.pluginManager.getPlugin("floodgate").isPresent
}