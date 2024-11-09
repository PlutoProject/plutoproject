package ink.pmc.framework.bridge.backend.world

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.backend.server.localServer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.WorldInfo
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeLocationImpl
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.bridge.world.InternalWorld
import ink.pmc.framework.utils.world.alias
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

internal fun World.getBridge(): BridgeWorld? {
    return localServer.getWorld(name)
}

internal fun WorldInfo.getBridge(): BridgeWorld? {
    return localServer.getWorld(name)
}

internal fun BridgeWorld.getBukkit(): World {
    check(serverState.isLocal) { "Only local location can be converted" }
    return Bukkit.getWorld(name)!!
}

internal fun Location.createBridge(): BridgeLocation {
    val localWorld = world.getBridge() ?: error("Local world not found: ${world.name}")
    return BridgeLocationImpl(Bridge.local, localWorld, x, y, z, yaw, pitch)
}

internal fun BridgeLocation.createBukkit(): Location {
    return Location(world.getBukkit(), x, y, z, yaw, pitch)
}

class BackendLocalWorld(private val actual: World) : InternalWorld() {
    override val server: BridgeServer = Bridge.local
    override val group: BridgeGroup? = server.group
    override val serverType: ServerType = ServerType.BACKEND
    override val serverState: ServerState = ServerState.LOCAL
    override val name: String = actual.name
    override val alias: String? = actual.alias
    override var spawnPoint: BridgeLocation
        get() = actual.spawnLocation.createBridge()
        set(_) = error("Unsupported")

    override fun getLocation(x: Double, y: Double, z: Double, yaw: Float, pitch: Float): BridgeLocation {
        return BridgeLocationImpl(server, this, x, y, z, yaw, pitch)
    }
}