package ink.pmc.interactive

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.Snapshot
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.Interactive
import ink.pmc.interactive.api.session.InventorySession
import ink.pmc.interactive.api.session.Session
import ink.pmc.interactive.api.session.SessionState
import ink.pmc.interactive.session.InventorySessionImpl
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.entity.Player

class InteractiveImpl : Interactive {

    private var disposed = false
    private val sessions = mutableListOf<Session<*>>()
    private val frameClock = BroadcastFrameClock()
    private val coroutineContext = interactiveScope.coroutineContext + frameClock
    private val recomposer = Recomposer(coroutineContext)
    private var observed = false
    private val observerHandle = Snapshot.registerGlobalWriteObserver {
        if (!observed) {
            observed = true
            interactiveScope.launch {
                observed = false
                Snapshot.sendApplyNotifications()
            }
        }
    }

    init {
        interactiveScope.launch(coroutineContext) {
            recomposer.runRecomposeAndApplyChanges()
        }

        interactiveScope.launch {
            while (!disposed) {
                frameClock.sendFrame(System.nanoTime())
                delay(5)
            }
        }
    }

    override fun get(player: Player): Session<*>? {
        require(!disposed) { "Interactive already disposed" }
        return sessions.firstOrNull { it.owner == player }
    }

    override fun has(player: Player): Boolean {
        require(!disposed) { "Interactive already disposed" }
        return get(player) != null
    }

    override fun startInventory(player: Player, contents: ComposableFunction): InventorySession {
        require(!disposed) { "Interactive already disposed" }
        if (has(player)) get(player)?.close()
        val session = InventorySessionImpl(player, recomposer, frameClock, contents)
        sessions.add(session)
        return session
    }

    override fun remove(session: Session<*>) {
        require(!disposed) { "Interactive already disposed" }
        if (session.state != SessionState.CLOSED) session.close()
        sessions.remove(session)
    }

    override fun dispose() {
        require(!disposed) { "Interactive already disposed" }
        disposed = true
        sessions.forEach { it.close() }
        recomposer.close()
        frameClock.cancel()
        observerHandle.dispose()
        coroutineContext.cancel()
    }

}