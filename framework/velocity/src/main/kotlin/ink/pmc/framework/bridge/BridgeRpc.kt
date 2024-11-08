package ink.pmc.framework.bridge

import com.google.protobuf.Empty
import ink.pmc.framework.bridge.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineImplBase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.proto.serverRegistrationAck
import ink.pmc.framework.bridge.server.ProxyRemoteBackendServer
import ink.pmc.framework.bridge.world.ProxyRemoteBackendWorld
import ink.pmc.framework.utils.platform.proxy
import ink.pmc.framework.utils.proto.empty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

object BridgeRpc : BridgeRpcCoroutineImplBase() {
    val notificationFlow = MutableSharedFlow<Notification>()

    override fun monitorNotification(request: Empty): Flow<Notification> {
        return notificationFlow
    }

    override suspend fun registerServer(request: ServerInfo): ServerRegistrationAck {
        if (Bridge.isServerRegistered(request.id)) {
            return serverRegistrationAck {
                idExisted = true
            }
        }
        val id = request.id
        val group = request.group?.let { BridgeGroupImpl(it) }
        val server = ProxyRemoteBackendServer(id, group).apply {
            worlds.addAll(request.worldsList.map {
                val world = ProxyRemoteBackendWorld(this, it.name, it.alias)
                val spawnPoint = BridgeLocationImpl(
                    this,
                    world,
                    it.spawnPoint.x,
                    it.spawnPoint.y,
                    it.spawnPoint.z,
                    it.spawnPoint.yaw,
                    it.spawnPoint.pitch
                )
                world.apply { this.spawnPoint = spawnPoint }
            })
            players.addAll(request.playersList.map {
                val name = it.name
                val world = it.location.world
                ProxyRemoteBackendPlayer(
                    proxy.getPlayer(it.uniqueId).get(),
                    this,
                    this.getWorld(world) ?: error("Cannot find world for $name: $world")
                )
            })
        }
        proxyBridge.servers.add(server)
        notificationFlow.emit(notification {
            serverRegistration = request
        })
        return serverRegistrationAck {
            ok = true
        }
    }

    override suspend fun heartbeat(request: ServerInfo): Empty {
        return empty
    }

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        return super.operatePlayer(request)
    }

    override suspend fun ackPlayerOperation(request: PlayerOperationAck): PlayerOperationAck {
        return super.ackPlayerOperation(request)
    }
}