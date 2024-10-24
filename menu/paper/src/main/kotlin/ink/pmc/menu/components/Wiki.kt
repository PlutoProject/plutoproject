package ink.pmc.menu.components

import androidx.compose.runtime.Composable
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.inventory.components.Item
import ink.pmc.interactive.api.inventory.modifiers.Modifier
import ink.pmc.interactive.api.inventory.modifiers.click.clickable
import ink.pmc.menu.messages.MAIN_MENU_ITEM_WIKI
import ink.pmc.menu.messages.MAIN_MENU_ITEM_WIKI_LORE
import ink.pmc.menu.messages.MAIN_MENU_WIKI
import ink.pmc.framework.utils.chat.MESSAGE_SOUND
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType

@Composable
@Suppress("FunctionName")
fun Wiki() {
    val player = LocalPlayer.current
    Item(
        material = Material.BOOK,
        name = MAIN_MENU_ITEM_WIKI,
        lore = MAIN_MENU_ITEM_WIKI_LORE,
        modifier = Modifier.clickable {
            if (clickType != ClickType.LEFT) return@clickable
            player.closeInventory()
            player.sendMessage(MAIN_MENU_WIKI)
            player.playSound(MESSAGE_SOUND)
        }
    )
}