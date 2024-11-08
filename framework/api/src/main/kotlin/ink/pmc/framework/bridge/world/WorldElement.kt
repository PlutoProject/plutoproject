package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.server.ServerElement

interface WorldElement : ServerElement {
    val world: BridgeWorld
}