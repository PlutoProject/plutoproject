package ink.pmc.framework.bridge

import com.google.protobuf.Empty
import ink.pmc.advkt.sound.key
import ink.pmc.advkt.sound.pitch
import ink.pmc.advkt.sound.volume
import ink.pmc.advkt.title.*
import ink.pmc.framework.bridge.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineImplBase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation.ContentCase.*
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase.OK
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase.UNSUPPORTED
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.proto.playerOperationResult
import ink.pmc.framework.bridge.proto.serverRegistrationAck
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.ProxyRemoteBackendServer
import ink.pmc.framework.bridge.server.localServer
import ink.pmc.framework.bridge.world.ProxyRemoteBackendWorld
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.platform.proxy
import ink.pmc.framework.utils.player.uuid
import ink.pmc.framework.utils.proto.empty
import ink.pmc.framework.utils.time.ticks
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withTimeoutOrNull
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

object BridgeRpc : BridgeRpcCoroutineImplBase() {
    private var isRunning = false
    private val heartbeatMap = ConcurrentHashMap<BridgeServer, Instant>()
    private val heartbeatCheckJob: Job
    private val playerOperationAck = Channel<PlayerOperationAck>()
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
        val local = localServer.getPlayer(request.player.uuid) ?: return playerOperationResult {
            playerNotOnline = true
        }
        val nonLocal = proxyBridge.getNonLocalPlayer(request.player.uuid)
        when (request.contentCase!!) {
            INFO_LOOKUP -> {
                notificationFlow.emit(notification {
                    playerOperation = request
                })
                val result = withTimeoutOrNull(10) {
                    for (ack in playerOperationAck) {
                        if (ack.server != request.server || ack.player != request.player) {
                            continue
                        }
                        return@withTimeoutOrNull when (ack.contentCase!!) {
                            OK -> ack.infoLookup
                            UNSUPPORTED -> playerOperationResult {
                                unsupported = true
                            }

                            PlayerOperationAck.ContentCase.CONTENT_NOT_SET -> {}
                        }
                    }
                }
                return when (result) {
                    is PlayerInfo -> playerOperationResult {
                        infoLookup = result
                    }

                    is PlayerOperationResult -> result
                    else -> error("Unexpected")
                }
            }

            SEND_MESSAGE -> local.sendMessage(MiniMessage.miniMessage().deserialize(request.server))
            SHOW_TITLE -> local.showTitle {
                val info = request.showTitle
                times {
                    fadeIn(info.fadeIn.ticks)
                    stay(info.stay.ticks)
                    fadeOut(info.fadeOut.ticks)
                }
                mainTitle(MiniMessage.miniMessage().deserialize(info.mainTitle))
                subTitle(MiniMessage.miniMessage().deserialize(info.subTitle))
            }

            PLAY_SOUND -> {
                local.playSound {
                    val info = request.playSound
                    key(Key.key(info.key))
                    volume(info.volume)
                    pitch(info.pitch)
                }
            }

            TELEPORT -> {
                val server = proxyBridge.getServer(request.teleport.server)
                if (server == null || !server.isOnline) {
                    return playerOperationResult {
                        serverNotOnline = true
                    }
                }
                val world = server.getWorld(request.teleport.world) ?: return playerOperationResult {
                    worldNotFound = true
                }
                val location = request.teleport.toImpl(server, world)
                nonLocal?.teleport(location) ?: return playerOperationResult {
                    unsupported = true
                }
            }

            PERFORM_COMMAND -> {
                nonLocal?.performCommand(request.performCommand) ?: return playerOperationResult {
                    unsupported = true
                }
            }

            CONTENT_NOT_SET -> {}
        }
        return playerOperationResult {
            ok = true
        }
    }

    override suspend fun ackPlayerOperation(request: PlayerOperationAck): Empty {
        playerOperationAck.send(request)
        return empty
    }
}