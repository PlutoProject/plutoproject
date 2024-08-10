package ink.pmc.interactive.session

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.Interactive
import ink.pmc.interactive.api.inventory.canvas.ClickHandler
import ink.pmc.interactive.api.inventory.canvas.ClickResult
import ink.pmc.interactive.api.inventory.layout.LayoutNode
import ink.pmc.interactive.api.inventory.modifiers.Constraints
import ink.pmc.interactive.api.inventory.modifiers.click.ClickScope
import ink.pmc.interactive.api.inventory.modifiers.drag.DragScope
import ink.pmc.interactive.api.inventory.nodes.InvNodeApplier
import ink.pmc.interactive.api.session.InventorySession
import ink.pmc.interactive.api.session.LocalClickHandler
import ink.pmc.interactive.api.session.LocalSessionProvider
import ink.pmc.interactive.api.session.SessionState
import ink.pmc.interactive.interactiveScope
import ink.pmc.utils.concurrent.submitSync
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class InventorySessionImpl(
    owner: Player,
    recomposer: Recomposer,
    frameClock: BroadcastFrameClock,
    contents: ComposableFunction
) : AbstractSession<LayoutNode>(owner, recomposer, frameClock, InvNodeApplier(LayoutNode()), contents),
    InventorySession, KoinComponent {

    private val manager by lazy { get<Interactive>() }

    override var composition: Composition = launchComposition()

    private fun launchComposition(): Composition {
        return Composition(applier, recomposer).apply {
            setContent {
                if (stateHolder == null) stateHolder = rememberSaveableStateHolder()
                stateHolder?.SaveableStateProvider(id) {
                    CompositionLocalProvider(
                        LocalSessionProvider provides this@InventorySessionImpl,
                        LocalClickHandler provides object : ClickHandler {
                            val rootNode = applier.current
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
                    ) {
                        contents()
                    }
                }
            }
            state = SessionState.WORKING
        }
    }

    init {
        renderLoop()
    }

    private fun renderLoop() {
        interactiveScope.launch {
            while (state == SessionState.WORKING) {
                frameClock.sendFrame(System.nanoTime())
                applier.current.apply {
                    measure(Constraints())
                    render()
                }
                delay(1)
            }
        }
    }

    override fun close() {
        state = SessionState.CLOSED
        composition.dispose()
        manager.remove(this)
        submitSync { owner.closeInventory() }
    }

    override fun pause() {
        state = SessionState.PAUSED
        composition.dispose()
        submitSync { owner.closeInventory() }
    }

    override fun resume() {
        state = SessionState.WORKING
        composition = launchComposition()
        renderLoop()
    }

}