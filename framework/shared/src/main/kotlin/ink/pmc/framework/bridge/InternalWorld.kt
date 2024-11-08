package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld

abstract class InternalWorld : BridgeWorld {
    abstract override var spawnPoint: BridgeLocation

    override fun equals(other: Any?): Boolean {
        if (other !is BridgeWorld) return false
        return other.server == server && other.name == name && other.serverType == serverType
    }

    override fun hashCode(): Int {
        var result = server.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + serverType.hashCode()
        return result
    }
}