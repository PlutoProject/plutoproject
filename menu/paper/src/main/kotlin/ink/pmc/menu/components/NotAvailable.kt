package ink.pmc.menu.components

import androidx.compose.runtime.Composable
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.interactive.api.inventory.components.Item
import ink.pmc.framework.utils.visual.mochaSubtext0
import ink.pmc.framework.utils.visual.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.Material

@Composable
@Suppress("FunctionName")
fun NotAvailable(name: Component?) {
    Item(
        material = Material.GRAY_STAINED_GLASS_PANE,
        name = name ?: component {
            text("不可用的功能") with mochaText without italic()
        },
        lore = listOf(
            Component.empty(),
            component {
                text("该功能暂时不可用") with mochaSubtext0 without italic()
            }
        )
    )
}