package ink.pmc.framework.interactive.inventory.components

import androidx.compose.runtime.*
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.framework.interactive.inventory.Item
import ink.pmc.framework.interactive.inventory.Modifier
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.utils.visual.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType

@Suppress("UNUSED", "FunctionName")
@Composable
fun Selector(
    title: Component,
    icon: Material = Material.STICK,
    description: List<Component> = listOf(),
    options: List<String>,
    default: Int = 0,
    highlightColor: TextColor = mochaBlue,
    goNext: () -> Unit,
    goPrevious: () -> Unit
) {
    require(options.isNotEmpty()) { "Options cannot be empty" }
    var current by remember { mutableStateOf(default) }
    Item(
        name = title,
        material = icon,
        lore = buildList {
            addAll(description)
            addAll(options.mapIndexed { index, s ->
                component {
                    text("» $s") with (if (current == index) highlightColor else mochaSubtext0) without italic()
                }
            })
            add(Component.empty())
            add(component {
                text("左键 ") with mochaLavender without italic()
                text("向后切换") with mochaText without italic()
            })
            add(component {
                text("右键 ") with mochaLavender without italic()
                text("向前切换") with mochaText without italic()
            })
        },
        modifier = Modifier.clickable {
            when (clickType) {
                ClickType.LEFT -> {
                    current = if (current == options.lastIndex) 0 else current + 1
                    goNext()
                }

                ClickType.RIGHT -> {
                    current = if (current == 0) options.lastIndex else current - 1
                    goPrevious()
                }

                else -> {}
            }
        }
    )
}