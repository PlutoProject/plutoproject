package ink.pmc.framework.interactive.inventory

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import ink.pmc.framework.frameworkPaper
import ink.pmc.framework.interactive.canvas.ClickHandler
import ink.pmc.framework.interactive.canvas.ClickResult
import ink.pmc.framework.interactive.canvas.GuiInventoryHolder
import ink.pmc.framework.interactive.click.ClickScope
import ink.pmc.framework.interactive.drag.DragScope
import ink.pmc.framework.interactive.layout.InventoryNode
import ink.pmc.framework.interactive.scope.BaseScope
import ink.pmc.framework.concurrent.submitSync
import ink.pmc.framework.interactive.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.bukkit.entity.Player
import java.util.logging.Level

class InventoryScope(owner: Player, contents: ComposableFunction) : BaseScope<InventoryNode>(owner, contents) {

    override val rootNode: InventoryNode = InventoryNode()
    override val nodeApplier: Applier<InventoryNode> = InventoryNodeApplier(rootNode) {
        if (isDisposed) return@InventoryNodeApplier
        runCatching {
            render()
            hasFrameWaiters = false
        }.onFailure {
            renderExceptionCallback(it)
        }
    }
    override val isPendingRefresh: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val clickHandler = object : ClickHandler {
        val rootNode = nodeApplier.current
        override suspend fun processClick(scope: ClickScope): ClickResult {
            val slot = scope.slot
            val width = rootNode.width
            return rootNode.children.fold(ClickResult()) { acc, node ->
                val w = node.width
                val x = if (w == 0) 0 else slot % width
                val y = if (w == 0) 0 else slot / width
                acc.mergeWith(rootNode.processClick(scope, x, y))
            }
        }

        override suspend fun processDrag(scope: DragScope) {
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

    private fun render() {
        nodeApplier.current.apply {
            measure(Constraints())
            render()
            owner.updateInventory()
        }
    }

    private fun renderExceptionCallback(e: Throwable) {
        owner.sendMessage(UI_RENDER_FAILED)
        frameworkPaper.logger.log(Level.SEVERE, "Inventory render failed while rendering for ${owner.name}", e)
        dispose()
    }

    override fun setPendingRefreshIfNeeded(state: Boolean) {
        if (state && !isPendingRefresh.value && owner.openInventory.topInventory.holder is GuiInventoryHolder) {
            isPendingRefresh.value = true
            return
        }
        if (!state && isPendingRefresh.value) {
            isPendingRefresh.value = false
            return
        }
    }

    override fun dispose() {
        if (isDisposed) return
        submitSync {
            if (!owner.isOnline) return@submitSync
            setPendingRefreshIfNeeded(true) // 防止 dispose 在事件中再次被调用造成 StackOverflowError
            owner.closeInventory()
        }
        super.dispose()
    }

}