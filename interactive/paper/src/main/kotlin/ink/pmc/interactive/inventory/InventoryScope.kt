package ink.pmc.interactive.inventory

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import ink.pmc.interactive.UI_RENDER_FAILED
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.LocalGuiScope
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.inventory.layout.InventoryNode
import ink.pmc.interactive.api.inventory.modifiers.Constraints
import ink.pmc.interactive.plugin
import ink.pmc.interactive.scope.BaseScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import java.util.logging.Level

class InventoryScope(owner: Player, contents: ComposableFunction) : BaseScope<InventoryNode>(owner, contents) {

    override val rootNode: InventoryNode = InventoryNode()
    override val nodeApplier: Applier<InventoryNode> = InventoryNodeApplier(rootNode) {}
    override val composition: Composition = Composition(nodeApplier, recomposer).apply {
        setContent {
            CompositionLocalProvider(
                LocalGuiScope provides this@InventoryScope,
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
        launch {
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
        this.dispose()
        owner.sendMessage(UI_RENDER_FAILED)
        plugin.logger.log(Level.SEVERE, "Inventory render loop failed while rendering for ${owner.name}", e)
    }

    override fun dispose() {
        super.dispose()
        owner.closeInventory()
    }

}