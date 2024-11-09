package ink.pmc.framework.bridge.player

import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult.ContentCase.*
import ink.pmc.framework.bridge.proto.playerOperation
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeLocationImpl
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.bridge.world.createInfo
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.proto.empty
import kotlinx.coroutines.Deferred
import java.util.*

abstract class RemoteBackendPlayer(
    uniqueId: UUID,
    name: String,
    server: BridgeServer,
    world: BridgeWorld? = null
) : RemotePlayer(uniqueId, name, server, world) {
    override val location: Deferred<BridgeLocation>
        get() = submitAsync<BridgeLocation> {
            check(server.isOnline) { "Server offline" }
            check(isOnline) { "Player offline" }
            checkNotNull(world) { "Uninitialized" }
            val result = operatePlayer(playerOperation {
                id = UUID.randomUUID().toString()
                playerUuid = uniqueId.toString()
                backend = true
                infoLookup = empty
            })
            val info = result.infoLookup
            check(info.hasLocation()) { "PlayerInfo missing required field" }
            val location = info.location
            when (result.contentCase!!) {
                OK -> BridgeLocationImpl(
                    server,
                    world!!,
                    location.x,
                    location.y,
                    location.z,
                    location.yaw,
                    location.pitch
                )

                else -> throw checkNoReturnResult(result) ?: error("Unexpected")
            }
        }


    private fun checkNoReturnResult(result: PlayerOperationResult): IllegalStateException? {
        return when (result.contentCase!!) {
            OK -> null
            PLAYER_OFFLINE -> error("Player offline: $name")
            SERVER_OFFLINE -> error("Server offline: ${server.id}")
            WORLD_NOT_FOUND -> error("World not found: ${world?.name}")
            UNSUPPORTED -> error("Unsupported")
            TIMEOUT -> error("Player operation timeout: $name")
            CONTENT_NOT_SET -> error("Received a PlayerOperationResult without content (player: $name")
        }
    }

    override suspend fun teleport(location: BridgeLocation) {
        check(server.isOnline) { "Server offline: ${server.id}" }
        check(isOnline) { "Player offline: $name" }
        val result = operatePlayer(playerOperation {
            id = UUID.randomUUID().toString()
            playerUuid = uniqueId.toString()
            backend = true
            teleport = location.createInfo()
        })
        checkNoReturnResult(result)?.let { throw it }
    }

    override suspend fun performCommand(command: String) {
        check(server.isOnline) { "Server offline: ${server.id}" }
        check(isOnline) { "Player offline: $name" }
        val result = operatePlayer(playerOperation {
            id = UUID.randomUUID().toString()
            playerUuid = uniqueId.toString()
            backend = true
            performCommand = command
        })
        checkNoReturnResult(result)?.let { throw it }
    }

    abstract suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult
}