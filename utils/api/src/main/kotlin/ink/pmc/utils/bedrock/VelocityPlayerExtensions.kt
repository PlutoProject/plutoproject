package ink.pmc.utils.bedrock

import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
val Player.isBedrock: Boolean
    get() = isFloodgatePlayer(this.uniqueId)

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

@Suppress("UNUSED")
fun Player.sendActionBar(component: Component, fallback: Component) {
    this.sendActionBar(this.fallback(component, fallback))
}

@Suppress("UNUSED")
fun Player.disconnect(component: Component, fallback: Component) {
    this.disconnect(this.fallback(component, fallback))
}