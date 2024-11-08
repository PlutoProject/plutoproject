package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.server.ServerElement
import kotlinx.coroutines.Deferred

interface WorldElement<T : WorldElement<T>> : ServerElement<T> {
    val world: Deferred<BridgeWorld>
}