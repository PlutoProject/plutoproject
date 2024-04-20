package ink.pmc.common.utils.bedrock

import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
val Player.isBedrock: Boolean
    get() = isBedrockSession(this.uniqueId)

@Suppress("UNUSED")
fun Player.fallback(original: Component, bedrock: Component): Component {
    if (this.isBedrock) {
        return bedrock
    }

    return original
}

@Suppress("UNUSED")
fun Player.sendMessage(component: Component, fallback: Component) {
    this.sendMessage(this.fallback(component, fallback))
}