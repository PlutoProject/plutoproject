package ink.pmc.framework.bridge.backend.listeners

import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.backend.server.localServer
import ink.pmc.framework.bridge.backend.world.BackendLocalWorld
import ink.pmc.framework.bridge.backend.world.getBridge
import ink.pmc.framework.bridge.localWorldNotFound
import ink.pmc.framework.bridge.proto.worldLoad
import ink.pmc.framework.bridge.world.createInfo
import org.bukkit.event.Listener
import org.bukkit.event.world.SpawnChangeEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent

@Suppress("UnusedReceiverParameter")
object BridgeWorldListener : Listener {
    suspend fun WorldLoadEvent.e() {
        val localWorld = BackendLocalWorld(world, localServer)
        localServer.worlds.add(localWorld)
        bridgeStub.updateWorldInfo(localWorld.createInfo())
    }

    suspend fun SpawnChangeEvent.e() {
        val localWorld = world.getBridge() ?: localWorldNotFound(world.name)
        bridgeStub.updateWorldInfo(localWorld.createInfo())
    }

    suspend fun WorldUnloadEvent.e() {
        val localWorld = localServer.getWorld(world.name) ?: localWorldNotFound(world.name)
        localServer.worlds.remove(localWorld)
        bridgeStub.unloadWorld(worldLoad {
            server = localServer.id
            world = localWorld.name
        })
    }
}