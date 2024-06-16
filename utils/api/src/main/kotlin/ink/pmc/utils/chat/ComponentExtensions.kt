package ink.pmc.utils.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

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

fun Component.replaceColor(targetColor: TextColor, newColor: TextColor): Component {
    val updatedComponent = if (this.style().color() == targetColor) {
        this.style(this.style().color(newColor))
    } else {
        this
    }

    return updatedComponent.children().fold(updatedComponent.children(emptyList())) { comp, child ->
        comp.append(child.replaceColor(targetColor, newColor))
    }
}

private val serializer = GsonComponentSerializer.gson()

val Component.json: String
    get() {
        return serializer.serialize(this)
    }