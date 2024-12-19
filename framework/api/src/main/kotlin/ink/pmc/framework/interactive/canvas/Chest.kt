package ink.pmc.framework.interactive.canvas

import androidx.compose.runtime.*
import ink.pmc.framework.interactive.GuiInventoryScope
import ink.pmc.framework.interactive.LocalGuiScope
import ink.pmc.framework.interactive.Modifier
import ink.pmc.framework.interactive.layout.Layout
import ink.pmc.framework.interactive.layout.Size
import ink.pmc.framework.interactive.nodes.InventoryCloseScope
import ink.pmc.framework.interactive.nodes.StaticMeasurePolicy
import ink.pmc.framework.interactive.onSizeChanged
import ink.pmc.framework.interactive.sizeIn
import ink.pmc.framework.interactive.state.IntCoordinates
import ink.pmc.framework.inventory.title
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

const val CHEST_WIDTH = 9
const val MIN_CHEST_HEIGHT = 1
const val MAX_CHEST_HEIGHT = 6

/**
 * A Chest GUI [Inventory] composable.
 *
 * @param title The title of the Chest inventory.
 * @param modifier The modifier for the Chest GUI, default is Modifier.
 * @param onClose The function to be executed when the Chest GUI is closed, default is an empty function.
 * @param content The content of the Chest GUI, defined as a Composable function.
 */
@Composable
@Suppress("UNCHECKED_CAST")
fun Chest(
    title: Component,
    modifier: Modifier = Modifier,
    onClose: (InventoryCloseScope.(player: Player) -> Unit) = {},
    content: @Composable () -> Unit,
) {
    val scope = LocalGuiScope.current as GuiInventoryScope
    var size by remember { mutableStateOf(Size()) }
    val constrainedModifier =
        Modifier.sizeIn(CHEST_WIDTH, CHEST_WIDTH, MIN_CHEST_HEIGHT, MAX_CHEST_HEIGHT).then(modifier)
            .onSizeChanged { if (size != it) size = it }

    val holder = rememberInventoryHolder(scope, onClose)

    // Create new inventory when any appropriate value changes

    // Draw nothing if empty
    if (size == Size()) {
        Layout(
            measurePolicy = StaticMeasurePolicy,
            modifier = constrainedModifier
        )
        return
    }

    val inventory: Inventory = remember(size) {
        Bukkit.createInventory(holder, CHEST_WIDTH * size.height, title).also {
            holder.activeInventory = it
        }
    }

    LaunchedEffect(title) {
        // This just sends a packet, doesn't need to be on sync thread
        inventory.viewers.forEach { it.openInventory.title(title) }
    }

    // PMC 的应用场景大概是不需要多个 viewers 的，这里就不实现了
    // 原 TODO: handle sending correct title when player list changes
    Inventory(
        inventory = inventory,
        modifier = constrainedModifier,
        gridToInventoryIndex = { (x, y) ->
            if (x !in 0 until CHEST_WIDTH || y !in 0 until size.height) null
            else x + y * CHEST_WIDTH
        },
        inventoryIndexToGrid = { index ->
            IntCoordinates(index % CHEST_WIDTH, index / CHEST_WIDTH)
        },
    ) {
        content()
    }
}
