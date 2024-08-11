package ink.pmc.interactive.scope

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.ObserverHandle
import androidx.compose.runtime.snapshots.Snapshot
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.Gui
import ink.pmc.interactive.api.GuiScope
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

@Suppress("UNUSED")
abstract class BaseScope<T>(
    override val owner: Player,
    private val contents: ComposableFunction
) : GuiScope<T>, KoinComponent {

    private val manager by inject<Gui>()

    override var isDisposed: Boolean = false
    var hasFrameWaiters: Boolean = false

    protected var renderSignal: CompletableDeferred<Unit>? = null
    private var hasSnapshotNotifications: Boolean = false
    private val frameClock: BroadcastFrameClock = BroadcastFrameClock { hasFrameWaiters = true }
    final override val coroutineContext: CoroutineContext = Dispatchers.Default + frameClock
    protected val recomposer: Recomposer = Recomposer(coroutineContext)

    private val observerHandle: ObserverHandle = Snapshot.registerGlobalWriteObserver {
        if (!hasSnapshotNotifications) {
            hasSnapshotNotifications = true
            launch {
                hasSnapshotNotifications = false
                Snapshot.sendApplyNotifications()
            }
        }
    }

    abstract val composition: Composition

    init {
        launch {
            recomposer.runRecomposeAndApplyChanges()
        }

        launch {
            while (!isDisposed) {
                frameClock.sendFrame(System.nanoTime())
                delay(5)
            }
        }
    }

    override fun dispose() {
        if (isDisposed) return
        if (hasFrameWaiters) {
            renderSignal = CompletableDeferred<Unit>().also {
                it.asCompletableFuture().join()
            }
        }

        cancel()
        composition.dispose()
        frameClock.cancel()
        recomposer.cancel()
        observerHandle.dispose()
        manager.removeScope(this)
    }

}