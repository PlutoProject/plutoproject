package ink.pmc.interactive.api.inventory.components

import androidx.compose.runtime.Composable
import ink.pmc.interactive.api.inventory.modifiers.Modifier
import ink.pmc.interactive.api.inventory.modifiers.click.clickable
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * An item that acts like a creative inventory item that can be copied on click.
 */
@Composable
fun CreativeItem(itemStack: ItemStack, modifier: Modifier = Modifier) {
    Item(itemStack, modifier.clickable {
        // Mimic all vanilla interactions
        val result: ItemStack? = when {
            (clickType.isShiftClick || clickType == ClickType.MIDDLE) && cursor == null ->
                itemStack.asQuantity(itemStack.maxStackSize)

            clickType == ClickType.MIDDLE -> return@clickable
            clickType == ClickType.SHIFT_LEFT && cursor != null && cursor.isSimilar(itemStack) ->
                cursor.asQuantity(cursor.maxStackSize)

            cursor == null -> itemStack.clone().asOne()
            clickType.isRightClick -> cursor.clone().subtract()
            clickType.isLeftClick && !cursor.isSimilar(itemStack) -> null

            else -> cursor.clone().add()
        }
        whoClicked.setItemOnCursor(result)
    })
}
