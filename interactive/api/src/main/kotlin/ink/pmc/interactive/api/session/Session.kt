package ink.pmc.interactive.api.session

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import ink.pmc.interactive.api.ComposableFunction
import org.bukkit.entity.Player
import java.util.*

enum class SessionState {

    EMPTY, WORKING, PAUSED, CLOSED

}

val LocalSessionProvider: ProvidableCompositionLocal<InventorySession> =
    staticCompositionLocalOf { error("No provider for session") }

@Suppress("UNUSED")
interface Session<T : Any> {

    val id: UUID
    val owner: Player
    val applier: Applier<T>
    val recomposer: Recomposer
    val frameClock: BroadcastFrameClock
    val stateHolder: SaveableStateHolder?
    val composition: Composition
    val state: SessionState
    val contents: ComposableFunction

    fun close()

    fun pause()

    fun resume()

}