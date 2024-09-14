package ink.pmc.menu.components

import androidx.compose.runtime.Composable
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.Item
import ink.pmc.framework.interactive.inventory.Modifier
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.utils.chat.MESSAGE_SOUND
import ink.pmc.menu.messages.YUME_MAIN_ITEM_WIKI_LORE
import ink.pmc.menu.messages.YUME_MAIN_WIKI
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType

@Composable
@Suppress("FunctionName")
fun Wiki() {
    val player = LocalPlayer.current
    Item(
        material = Material.BOOK,
        name = YUME_MAIN_WIKI,
        lore = YUME_MAIN_ITEM_WIKI_LORE,
        modifier = Modifier.clickable {
            if (clickType != ClickType.LEFT) return@clickable
            player.closeInventory()
            player.sendMessage(YUME_MAIN_WIKI)
            player.playSound(MESSAGE_SOUND)
        }
    )
}