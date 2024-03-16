package ink.pmc.common.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig

fun Component.replace(string: String, component: Component): Component {
    val replaceConfig = TextReplacementConfig.builder()
        .match(string)
        .replacement(component)
        .build()

    return this.replaceText(replaceConfig)
}

fun Component.replace(string: String, text: String): Component {
    val replaceConfig = TextReplacementConfig.builder()
        .match(string)
        .replacement(Component.text(text))
        .build()

    return this.replaceText(replaceConfig)
}