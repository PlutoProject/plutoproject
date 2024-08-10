package ink.pmc.interactive

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.Snapshot
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.Interactive
import ink.pmc.interactive.api.session.Session
import ink.pmc.interactive.api.session.SessionState
import ink.pmc.interactive.api.inventory.layout.LayoutNode
import ink.pmc.interactive.api.session.InventorySession
import ink.pmc.interactive.session.InventorySessionImpl
import kotlinx.coroutines.launch
import org.bukkit.entity.Player

class InteractiveImpl : Interactive {

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
    }

    override fun get(player: Player): Session<*>? {
        return sessions.firstOrNull { it.owner == player }
    }

    override fun has(player: Player): Boolean {
        return get(player) != null
    }

    override fun startInventory(player: Player, contents: ComposableFunction): InventorySession {
        if (has(player)) get(player)?.close()
        val session = InventorySessionImpl(player, recomposer, frameClock, contents)
        sessions.add(session)
        return session
    }

    override fun remove(session: Session<*>) {
        if (session.state != SessionState.CLOSED) session.close()
        sessions.remove(session)
    }

    override fun dispose() {
        sessions.forEach { it.close() }
        recomposer.close()
        observerHandle.dispose()
    }

}