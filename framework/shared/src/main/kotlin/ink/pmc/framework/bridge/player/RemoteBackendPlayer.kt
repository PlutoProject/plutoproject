package ink.pmc.framework.bridge.player

import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult.StatusCase.*
import ink.pmc.framework.bridge.proto.playerOperation
import ink.pmc.framework.bridge.remoteWorldNotFound
import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeLocationImpl
import ink.pmc.framework.bridge.world.createInfo
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.proto.empty
import kotlinx.coroutines.Deferred
import java.util.*

abstract class RemoteBackendPlayer : RemotePlayer() {
    override val location: Deferred<BridgeLocation>
        get() = submitAsync<BridgeLocation> {
            check(server.isOnline) { "Server offline" }
            check(isOnline) { "Player offline" }
            checkNotNull(world) { "Uninitialized" }
            val result = operatePlayer(playerOperation {
                id = UUID.randomUUID().toString()
                executor = server.id
                playerUuid = uniqueId.toString()
                backend = true
                infoLookup = empty
            })
            val info = result.infoLookup
            when (result.statusCase!!) {
                OK -> {
                    check(info.hasLocation()) { "PlayerInfo missing required field" }
                    val loc = info.location
                    BridgeLocationImpl(server, world!!, loc.x, loc.y, loc.z, loc.yaw, loc.pitch)
                }

                else -> throw checkNoReturnResult(result) ?: error("Unexpected")
            }
        }


    private fun checkNoReturnResult(result: PlayerOperationResult): IllegalStateException? {
        return when (result.statusCase!!) {
            OK -> null
            PLAYER_OFFLINE -> error("Player offline: $name")
            SERVER_OFFLINE -> error("Remote server offline: ${server.id}")
            WORLD_NOT_FOUND -> remoteWorldNotFound("${world?.name}", server.id)
            UNSUPPORTED -> error("Unsupported")
            TIMEOUT -> error("Player operation timeout: $name")
            MISSING_FIELDS -> error("Missing fields")
            STATUS_NOT_SET -> error("Received a PlayerOperationResult without status (player: $name")
        }
    }

    override suspend fun teleport(location: BridgeLocation) {
        check(server.isOnline) { "Server offline: ${server.id}" }
        check(isOnline) { "Player offline: $name" }
        val result = operatePlayer(playerOperation {
            id = UUID.randomUUID().toString()
            executor = server.id
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
            executor = server.id
            playerUuid = uniqueId.toString()
            backend = true
            performCommand = command
        })
        checkNoReturnResult(result)?.let { throw it }
    }
}