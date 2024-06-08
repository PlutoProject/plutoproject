package ink.pmc.utils.bedrock

import ink.pmc.utils.chat.replaceColor
import net.kyori.adventure.text.Component

fun Component.useFallbackColors(): Component {
    return bedrockColorMapping.entries.fold(this) { currentComponent, it ->
        currentComponent.replaceColor(it.key, it.value)
    }
}