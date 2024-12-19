package ink.pmc.framework.interactive.components

import androidx.compose.runtime.Composable
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.framework.interactive.Item
import ink.pmc.framework.chat.mochaMaroon
import ink.pmc.framework.chat.mochaSubtext0
import net.kyori.adventure.text.Component
import org.bukkit.Material

@Suppress("FunctionName")
@Composable
fun NotAvailable(
    material: Material,
    name: Component
) {
    Item(
        material = material,
        name = name,
        lore = buildList {
            add(component {
                text("因服务器内部问题，此功能不可用") with mochaMaroon without italic()
            })
            add(component {
                text("请将其反馈给管理组以便我们尽快解决") with mochaSubtext0 without italic()
            })
        }
    )
}