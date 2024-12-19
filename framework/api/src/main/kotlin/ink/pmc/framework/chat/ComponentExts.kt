package ink.pmc.framework.chat

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

@JvmName("replaceNullableComponent")
fun Component.replace(pattern: String, component: Component?): Component {
    return replace(pattern, component ?: Component.text("null"))
}

fun Component.replace(string: String, text: String): Component {
    val replaceConfig = TextReplacementConfig.builder()
        .match(string)
        .replacement(Component.text(text))
        .build()
    return this.replaceText(replaceConfig)
}

fun Component.replace(string: String, content: Any?): Component {
    return replace(string, content.toString())
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

fun Component.splitLines(): Collection<Component> {
    var curr = Component.empty()
    return buildList {
        val root = this@splitLines.children(emptyList())
        if (root != Component.empty()) {
            add(root)
        }
        children().forEach { child ->
            if (child == Component.newline()) {
                if (curr != Component.empty()) add(curr)
                curr = Component.empty()
                return@forEach
            }
            curr = curr.append(child)
        }
        if (curr != Component.empty()) {
            add(curr)
        }
    }
}

fun Collection<Component>.replace(pattern: String, content: Component): Collection<Component> {
    return map { it.replace(pattern, content) }
}

fun Collection<Component>.replace(pattern: String, content: Any?): Collection<Component> {
    return map { it.replace(pattern, content) }
}

@JvmName("replaceNullableComponent")
fun Collection<Component>.replace(pattern: String, content: Component?): Collection<Component> {
    return map { it.replace(pattern, content ?: Component.text("null")) }
}

val gsonComponentSerializer = GsonComponentSerializer.gson()