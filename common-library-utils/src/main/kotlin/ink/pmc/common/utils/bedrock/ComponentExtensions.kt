package ink.pmc.common.utils.bedrock

import ink.pmc.common.utils.chat.replaceColor
import net.kyori.adventure.text.Component

fun Component.useFallbackColors(): Component {
    return bedrockColorMapping.entries.fold(this) { currentComponent, it ->
        currentComponent.replaceColor(it.key, it.value)
    }
}