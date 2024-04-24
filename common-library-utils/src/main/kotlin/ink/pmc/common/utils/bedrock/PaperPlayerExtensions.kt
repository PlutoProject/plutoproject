package ink.pmc.common.utils.bedrock

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

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

@Suppress("UNUSED")
fun Player.sendActionBar(component: Component, fallback: Component) {
    this.sendActionBar(this.fallback(component, fallback))
}

@Suppress("UNUSED")
fun Player.kick(component: Component, fallback: Component) {
    this.kick(this.fallback(component, fallback))
}