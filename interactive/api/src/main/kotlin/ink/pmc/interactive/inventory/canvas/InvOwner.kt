package ink.pmc.interactive.inventory.canvas

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import ink.pmc.interactive.inventory.layout.LayoutNode
import ink.pmc.interactive.inventory.modifiers.click.ClickScope
import ink.pmc.interactive.inventory.modifiers.Constraints
import ink.pmc.interactive.inventory.modifiers.drag.DragScope
import ink.pmc.interactive.inventory.nodes.InvNodeApplier
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

val LocalClickHandler: ProvidableCompositionLocal<ClickHandler> =
    staticCompositionLocalOf { error("No provider for local click handler") }
val LocalCanvas: ProvidableCompositionLocal<Canvas?> =
    staticCompositionLocalOf { null }
val LocalInvOwner: ProvidableCompositionLocal<InvOwner> =
    staticCompositionLocalOf { error("No provider for InvOwner") }

data class ClickResult(val cancelBukkitEvent: Boolean? = null) {

    fun mergeWith(other: ClickResult) = ClickResult(
        // Prioritize true > false > null
        cancelBukkitEvent = (cancelBukkitEvent ?: other.cancelBukkitEvent)?.or(other.cancelBukkitEvent ?: false)
    )

}

interface ClickHandler {

    fun processClick(scope: ClickScope): ClickResult
    fun processDrag(scope: DragScope)

}

@InvUiScopeMarker
class InvOwner : CoroutineScope {

    private var hasFrameWaiters = false
    private val clock = BroadcastFrameClock { hasFrameWaiters = true }
    private val composeScope = CoroutineScope(Dispatchers.Default) + clock
    override val coroutineContext: CoroutineContext = composeScope.coroutineContext

    private val rootNode = LayoutNode()

    private var running = false
    private val recomposer = Recomposer(coroutineContext)
    private val composition = Composition(InvNodeApplier(rootNode), recomposer)

    private var applyScheduled = false
    private val snapshotHandle = Snapshot.registerGlobalWriteObserver {
        if (!applyScheduled) {
            applyScheduled = true
            composeScope.launch {
                applyScheduled = false
                Snapshot.sendApplyNotifications()
            }
        }
    }

    private var exitScheduled = false

    fun exit() {
        exitScheduled = true
    }

    fun start(content: @Composable () -> Unit) {
        !running || return
        running = true

        InvScopes.scopes += composeScope
        launch {
            recomposer.runRecomposeAndApplyChanges()
        }

        launch {
            setContent(content)
            while (!exitScheduled) {
                if (hasFrameWaiters) {
                    hasFrameWaiters = false
                    clock.sendFrame(System.nanoTime()) // Frame time value is not used by Compose runtime.
                    rootNode.measure(Constraints())
                    rootNode.render()
                }
                delay(50)
            }
            running = false
            recomposer.close()
            snapshotHandle.dispose()
            composition.dispose()
            composeScope.cancel()
        }
    }

    private fun setContent(content: @Composable () -> Unit) {
        hasFrameWaiters = true
        composition.setContent {
            CompositionLocalProvider(LocalClickHandler provides object : ClickHandler {
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
            }) {
                content()
            }
        }
    }

}

fun inv(
    content: @Composable () -> Unit
): InvOwner {
    return InvOwner().apply {
        start {
            InvCompositionLocal(this) {
                content()
            }
        }
    }
}

@Composable
@Suppress("FunctionName")
fun InvCompositionLocal(owner: InvOwner, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalInvOwner provides owner
    ) {
        content()
    }
}
