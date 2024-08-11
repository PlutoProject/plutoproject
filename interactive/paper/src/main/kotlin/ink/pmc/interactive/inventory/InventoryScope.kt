package ink.pmc.interactive.inventory

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import ink.pmc.interactive.UI_RENDER_FAILED
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.LocalGuiScope
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.inventory.LocalClickHandler
import ink.pmc.interactive.api.inventory.canvas.ClickHandler
import ink.pmc.interactive.api.inventory.canvas.ClickResult
import ink.pmc.interactive.api.inventory.layout.InventoryNode
import ink.pmc.interactive.api.inventory.modifiers.Constraints
import ink.pmc.interactive.api.inventory.modifiers.click.ClickScope
import ink.pmc.interactive.api.inventory.modifiers.drag.DragScope
import ink.pmc.interactive.interactiveScope
import ink.pmc.interactive.plugin
import ink.pmc.interactive.scope.BaseScope
import ink.pmc.utils.concurrent.submitSync
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import java.util.logging.Level

class InventoryScope(owner: Player, contents: ComposableFunction) : BaseScope<InventoryNode>(owner, contents) {

    override val rootNode: InventoryNode = InventoryNode()
    override val nodeApplier: Applier<InventoryNode> = InventoryNodeApplier(rootNode) {}
    override val isPendingRefresh: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val clickHandler = object : ClickHandler {
        val rootNode = nodeApplier.current
        override fun processClick(scope: ClickScope): ClickResult {
            val slot = scope.slot
            val width = rootNode.width
            return rootNode.children.fold(ClickResult()) { acc, node ->
                val w = node.width
                val x = if (w == 0) 0 else slot % width
                val y = if (w == 0) 0 else slot / width
                acc.mergeWith(rootNode.processClick(scope, x, y))
            }
        }

        override fun processDrag(scope: DragScope) {
            rootNode.processDrag(scope)
        }
    }

    override val composition: Composition = Composition(nodeApplier, recomposer).apply {
        setContent {
            CompositionLocalProvider(
                LocalGuiScope provides this@InventoryScope,
                LocalClickHandler provides clickHandler,
                LocalPlayer provides owner
            ) {
                runCatching {
                    contents()
                }.onFailure {
                    renderExceptionCallback(it)
                }
            }
        }
    }

    init {
        renderLoop()
    }

    private fun render() {
        nodeApplier.current.apply {
            measure(Constraints())
            render()
        }
    }

    private fun renderLoop() {
        interactiveScope.launch(coroutineContext) {
            runCatching {
                while (!isDisposed) {
                    render()
                    delay(5)
                }
            }.onFailure {
                renderExceptionCallback(it)
            }
        }
    }

    private fun renderExceptionCallback(e: Throwable) {
        owner.sendMessage(UI_RENDER_FAILED)
        plugin.logger.log(Level.SEVERE, "Inventory render loop failed while rendering for ${owner.name}", e)
        dispose()
    }

    override fun dispose() {
        super.dispose()
        submitSync { owner.closeInventory() }
    }

}