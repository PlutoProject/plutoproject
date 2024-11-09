package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.WorldInfo
import ink.pmc.framework.bridge.proto.worldInfo

fun BridgeWorld.toInfo(): WorldInfo {
    val world = this
    return worldInfo {
        this.server = world.server.id
        name = world.name
        world.alias?.also { alias = it }
        spawnPoint = world.spawnPoint.toInfo()
    }
}

abstract class InternalWorld : BridgeWorld {
    abstract override var spawnPoint: BridgeLocation

    override fun equals(other: Any?): Boolean {
        if (other !is BridgeWorld) return false
        return other.server == server
                && other.name == name
                && other.serverState == serverState
                && other.serverType == serverType
    }

    override fun hashCode(): Int {
        var result = server.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + serverState.hashCode()
        result = 31 * result + serverType.hashCode()
        return result
    }
}