package ink.pmc.framework.bridge

import com.google.protobuf.Empty
import ink.pmc.framework.bridge.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineImplBase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.proto.serverRegistrationAck
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ProxyRemoteBackendServer
import ink.pmc.framework.bridge.world.ProxyRemoteBackendWorld
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.platform.proxy
import ink.pmc.framework.utils.proto.empty
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

object BridgeRpc : BridgeRpcCoroutineImplBase() {
    private var isRunning = false
    private val heartbeatMap = ConcurrentHashMap<BridgeServer, Instant>()
    private val heartbeatCheckJob: Job
    val notificationFlow = MutableSharedFlow<Notification>()

    override fun monitorNotification(request: Empty): Flow<Notification> {
        return notificationFlow
    }

    init {
        isRunning = true
        // Heartbeat check loop
        heartbeatCheckJob = submitAsync {
            while (isRunning) {
                delay(5.seconds)
                proxyBridge.servers.forEach {
                    val time = heartbeatMap[it]
                    val requirement = Instant.now().minusSeconds(5)
                    if (time == null || time.isBefore(requirement)) {
                        (it as ProxyRemoteBackendServer).isOnline = false
                        notificationFlow.emit(notification {
                            serverOffline = it.id
                        })
                    }
                }
            }
        }
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
            updateWorlds(request)
            updatePlayers(request)
        }
        proxyBridge.servers.add(server)
        heartbeatMap[server] = Instant.now()
        notificationFlow.emit(notification {
            serverRegistration = request
        })
        return serverRegistrationAck {
            ok = true
        }
    }

    private fun ProxyRemoteBackendServer.updateWorlds(info: ServerInfo) {
        worlds.addAll(info.worldsList.map {
            val world = ProxyRemoteBackendWorld(this, it.name, it.alias)
            val spawnPoint = it.spawnPoint.toImpl(this, world)
            world.apply { this.spawnPoint = spawnPoint }
        })
    }

    private fun ProxyRemoteBackendServer.updatePlayers(info: ServerInfo) {
        players.addAll(info.playersList.map {
            val name = it.name
            val world = it.location.world
            ProxyRemoteBackendPlayer(
                proxy.getPlayer(it.uniqueId).get(),
                this,
                this.getWorld(world) ?: error("Cannot find world for $name: $world")
            )
        })
    }

    override suspend fun heartbeat(request: ServerInfo): Empty {
        val server = proxyBridge.getServer(request.id) as ProxyRemoteBackendServer? ?: return empty
        heartbeatMap[server] = Instant.now()
        server.isOnline = true
        server.updateWorlds(request)
        server.updatePlayers(request)
        notificationFlow.emit(notification {
            serverInfoUpdate = request
        })
        return empty
    }

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        return super.operatePlayer(request)
    }

    override suspend fun ackPlayerOperation(request: PlayerOperationAck): PlayerOperationAck {
        return super.ackPlayerOperation(request)
    }
}