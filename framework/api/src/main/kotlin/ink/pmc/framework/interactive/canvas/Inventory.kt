package ink.pmc.framework.interactive.canvas

import androidx.compose.runtime.*
import ink.pmc.framework.concurrent.submitSync
import ink.pmc.framework.concurrent.sync
import ink.pmc.framework.interactive.*
import ink.pmc.framework.interactive.click.ClickScope
import ink.pmc.framework.interactive.drag.DragScope
import ink.pmc.framework.interactive.layout.Layout
import ink.pmc.framework.interactive.layout.Renderer
import ink.pmc.framework.interactive.nodes.BaseInventoryNode
import ink.pmc.framework.interactive.nodes.InventoryCloseScope
import ink.pmc.framework.interactive.nodes.StaticMeasurePolicy
import ink.pmc.framework.interactive.state.IntCoordinates
import ink.pmc.framework.time.ticks
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.inventory.Inventory

val LocalInventory: ProvidableCompositionLocal<Inventory> =
    compositionLocalOf { error("No local inventory defined") }

/**
 * A layout composable that handles opening and closing an inventory for a set of players.
 *
 * @param inventory The bukkit inventory to be displayed.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
@Suppress("UNCHECKED_CAST")
fun Inventory(
    inventory: Inventory,
    modifier: Modifier = Modifier,
    gridToInventoryIndex: (IntCoordinates) -> Int?,
    inventoryIndexToGrid: (Int) -> IntCoordinates,
    content: @Composable () -> Unit,
) {
    val player = LocalPlayer.current
    val scope = LocalGuiScope.current
    val canvas = remember { MapBackedCanvas() }

    LaunchedEffect(player) {
        sync {
            scope.setPendingRefreshIfNeeded(true)
            player.openInventory(inventory)
        }
    }

    val renderer = object : Renderer {
        override fun Canvas.render(node: BaseInventoryNode) {
            canvas.startRender()
        }

        override fun Canvas.renderAfterChildren(node: BaseInventoryNode) {
            val items = canvas.getCoordinates()
            repeat(inventory.size) { index ->
                val coords = inventoryIndexToGrid(index)
                if (items[coords] == null) inventory.setItem(index, null)
            }
            for ((coords, item) in items) {
                val index = gridToInventoryIndex(coords) ?: continue
                if (index !in 0..<inventory.size) continue
                val invItem = inventory.getItem(index)
                if (invItem != item) inventory.setItem(index, item)
            }
        }
    }

    CompositionLocalProvider(
        LocalCanvas provides canvas,
        LocalInventory provides inventory
    ) {
        Layout(
            measurePolicy = StaticMeasurePolicy,
            renderer = renderer,
            modifier = modifier,
            content = content,
        )
    }
}

@Composable
inline fun rememberInventoryHolder(
    session: GuiInventoryScope,
    crossinline onClose: InventoryCloseScope.(Player) -> Unit = {},
): GuiInventoryHolder {
    val clickHandler = LocalClickHandler.current
    return remember(clickHandler) {
        object : GuiInventoryHolder(session) {
            override suspend fun processClick(scope: ClickScope, event: Cancellable) {
                val clickResult = clickHandler.processClick(scope)
            }

            override suspend fun processDrag(scope: DragScope) {
                clickHandler.processDrag(scope)
            }

            override fun onClose(player: Player) {
                val scope = object : InventoryCloseScope {
                    override fun reopen() {
                        // TODO don't think this reference updates properly in the remember block
                        if (player.openInventory.topInventory != inventory) {
                            player.openInventory(inventory)
                        }
                    }
                }
                submitSync {
                    delay(1.ticks)
                    onClose.invoke(scope, player)
                }
            }
        }
    }
}
