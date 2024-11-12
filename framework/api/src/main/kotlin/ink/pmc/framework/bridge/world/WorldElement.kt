package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.server.ServerElement

interface WorldElement<T : WorldElement<T>> : ServerElement<T> {
    val world: BridgeWorld?
}