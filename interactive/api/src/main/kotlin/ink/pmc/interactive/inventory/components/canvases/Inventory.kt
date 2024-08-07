package ink.pmc.interactive.inventory.components.canvases

import androidx.compose.runtime.*
import ink.pmc.interactive.inventory.components.state.IntCoordinates
import ink.pmc.interactive.inventory.layout.Layout
import ink.pmc.interactive.inventory.layout.Renderer
import ink.pmc.interactive.inventory.modifiers.Modifier
import ink.pmc.interactive.inventory.modifiers.click.ClickScope
import ink.pmc.interactive.inventory.modifiers.drag.DragScope
import ink.pmc.interactive.inventory.nodes.InvNode
import ink.pmc.interactive.inventory.nodes.InventoryCloseScope
import ink.pmc.interactive.inventory.nodes.StaticMeasurePolicy
import ink.pmc.interactive.inventory.canvas.*
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.concurrent.sync
import ink.pmc.utils.time.ticks
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

val LocalInventory: ProvidableCompositionLocal<Inventory> =
    compositionLocalOf { error("No local inventory defined") }

/**
 * A layout composable that handles opening and closing an inventory for a set of players.
 *
 * @param inventory The bukkit inventory to be displayed.
 * @param viewers The set of players who will view the inventory.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun Inventory(
    inventory: Inventory,
    viewers: Set<Player>,
    modifier: Modifier = Modifier,
    gridToInventoryIndex: (IntCoordinates) -> Int?,
    inventoryIndexToGrid: (Int) -> IntCoordinates,
    content: @Composable () -> Unit,
) {
    // Close inventory when it switches to a new one
    DisposableEffect(inventory) {
        onDispose {
            submitAsync {
                inventory.close()
            }
        }
    }
    // Manage opening inventory for new viewers or when inventory changes
    LaunchedEffect(viewers, inventory) {
        val oldViewers = inventory.viewers.toSet()
        sync {
            // Close inventory for removed viewers
            (oldViewers - viewers).forEach {
                it.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            }

            // Open inventory for new viewers
            (viewers - oldViewers).forEach {
                it.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                it.openInventory(inventory)
            }
        }
    }
    val canvas = remember { MapBackedCanvas() }

    CompositionLocalProvider(
        LocalCanvas provides canvas,
        LocalInventory provides inventory
    ) {
        Layout(
            measurePolicy = StaticMeasurePolicy,
            renderer = object : Renderer {
                override fun Canvas.render(node: InvNode) {
                    canvas.startRender()
                }

                override fun Canvas.renderAfterChildren(node: InvNode) {
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
            },
            modifier = modifier,
            content = content,
        )
    }
}

@Composable
inline fun rememberInventoryHolder(
    viewers: Set<Player>,
    crossinline onClose: InventoryCloseScope.(Player) -> Unit = {},
): InvInventoryHolder {
    val clickHandler = LocalClickHandler.current
    return remember(clickHandler) {
        object : InvInventoryHolder() {
            override fun processClick(scope: ClickScope, event: Cancellable) {
                val clickResult = clickHandler.processClick(scope)
            }

            override fun processDrag(scope: DragScope) {
                clickHandler.processDrag(scope)
            }

            override fun onClose(player: Player) {
                val scope = object : InventoryCloseScope {
                    override fun reopen() {
                        //TODO don't think this reference updates properly in the remember block
                        viewers.filter { it.openInventory.topInventory != inventory }
                            .forEach { it.openInventory(inventory) }
                    }
                }
                submitAsync {
                    delay(1.ticks)
                    onClose.invoke(scope, player)
                }
            }
        }
    }
}
