package ink.pmc.framework.interactive.scope

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.ObserverHandle
import androidx.compose.runtime.snapshots.Snapshot
import ink.pmc.framework.interactive.ComposableFunction
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.interactive.GuiScope
import kotlinx.coroutines.*
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

@Suppress("UNUSED")
abstract class BaseScope<T>(
    override val owner: Player,
    private val contents: ComposableFunction
) : GuiScope<T>, KoinComponent {

    var hasFrameWaiters: Boolean = false
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val manager by inject<GuiManager>()
    private var hasSnapshotNotifications: Boolean = false
    private val frameClock: BroadcastFrameClock = BroadcastFrameClock { hasFrameWaiters = true }
    private val coroutineContext: CoroutineContext = Dispatchers.Default + frameClock
    private val observerHandle: ObserverHandle = Snapshot.registerGlobalWriteObserver {
        if (!hasSnapshotNotifications) {
            hasSnapshotNotifications = true
            coroutineScope.launch(coroutineContext) {
                hasSnapshotNotifications = false
                Snapshot.sendApplyNotifications()
            }
        }
    }
    protected val recomposer: Recomposer = Recomposer(coroutineContext)

    override var isDisposed: Boolean = false
    abstract val composition: Composition

    init {
        coroutineScope.launch(coroutineContext) {
            recomposer.runRecomposeAndApplyChanges()
        }

        coroutineScope.launch(coroutineContext) {
            while (!isDisposed) {
                frameClock.sendFrame(System.nanoTime())
                delay(5)
            }
        }
    }

    override fun dispose() {
        if (isDisposed) return
        isDisposed = true
        coroutineScope.cancel()
        composition.dispose()
        frameClock.cancel()
        recomposer.cancel()
        observerHandle.dispose()
        manager.removeScope(this)
    }

}