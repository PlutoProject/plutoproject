package ink.pmc.menu.components

import androidx.compose.runtime.Composable
import ink.pmc.advkt.component.*
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.Item
import ink.pmc.framework.interactive.inventory.Modifier
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.utils.chat.MESSAGE_SOUND
import ink.pmc.framework.utils.concurrent.sync
import ink.pmc.framework.utils.visual.mochaLavender
import ink.pmc.framework.utils.visual.mochaSubtext0
import ink.pmc.framework.utils.visual.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType

@Composable
@Suppress("FunctionName")
fun Wiki() {
    val player = LocalPlayer.current
    Item(
        material = Material.BOOK,
        name = component {
            text("星社百科") with mochaText without italic()
        },
        lore = buildList {
            add(component {
                text("服务器的百科全书") with mochaSubtext0 without italic()
            })
            add(component {
                text("里面记载了有关星社的一切") with mochaSubtext0 without italic()
            })
            add(Component.empty())
            add(component {
                text("左键 ") with mochaLavender without italic()
                text("获取百科链接") with mochaText without italic()
            })
        },
        modifier = Modifier.clickable {
            if (clickType != ClickType.LEFT) return@clickable
            player.sendMessage(component {
                text("点此打开星社百科") with mochaLavender with underlined() with openUrl("https://wiki.plutomc.club/")
            })
            player.playSound(MESSAGE_SOUND)
            sync {
                player.closeInventory()
            }
        }
    )
}