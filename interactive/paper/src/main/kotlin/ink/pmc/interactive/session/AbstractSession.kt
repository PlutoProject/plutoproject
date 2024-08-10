package ink.pmc.interactive.session

import androidx.compose.runtime.Applier
import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.saveable.SaveableStateHolder
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.session.Session
import ink.pmc.interactive.api.session.SessionState
import org.bukkit.entity.Player
import java.util.*

abstract class AbstractSession<T : Any>(
    override val owner: Player,
    override val recomposer: Recomposer,
    override val frameClock: BroadcastFrameClock,
    override val applier: Applier<T>,
    override val contents: ComposableFunction
) : Session<T> {

    override val id: UUID = UUID.randomUUID()
    override var stateHolder: SaveableStateHolder? = null
    abstract override var composition: Composition
    override var state: SessionState = SessionState.EMPTY

}