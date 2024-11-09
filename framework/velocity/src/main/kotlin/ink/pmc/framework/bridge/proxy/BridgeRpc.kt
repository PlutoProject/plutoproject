package ink.pmc.framework.bridge.proxy

import com.google.protobuf.Empty
import ink.pmc.advkt.sound.key
import ink.pmc.advkt.sound.pitch
import ink.pmc.advkt.sound.volume
import ink.pmc.advkt.title.*
import ink.pmc.framework.bridge.*
import ink.pmc.framework.bridge.proxy.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineImplBase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation.ContentCase.*
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase.OK
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase.UNSUPPORTED
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.proto.playerOperationResult
import ink.pmc.framework.bridge.proto.serverRegistrationAck
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.proxy.server.ProxyRemoteBackendServer
import ink.pmc.framework.bridge.proxy.server.localServer
import ink.pmc.framework.bridge.proxy.world.ProxyRemoteBackendWorld
import ink.pmc.framework.frameworkLogger
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.platform.proxy
import ink.pmc.framework.utils.player.switchServer
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
    private val notificationFlow = MutableSharedFlow<Notification>()

    override fun monitorNotification(request: Empty): Flow<Notification> {
        return notificationFlow
    }

    suspend fun notify(notification: Notification) {
        notificationFlow.emit(notification)
    }

    init {
        isRunning = true
        // Heartbeat check loop
        heartbeatCheckJob = submitAsync {
            while (isRunning) {
                delay(5.seconds)
                proxyBridge.servers.forEach {
                    if (it.state.isLocal) return@forEach
                    val remoteServer = it as InternalServer
                    val time = heartbeatMap[it]
                    val requirement = Instant.now().minusSeconds(5)
                    if (time == null || time.isBefore(requirement)) {
                        remoteServer.isOnline = false
                        notificationFlow.emit(notification {
                            serverOffline = it.id
                        })
                        frameworkLogger.warning("Server ${remoteServer.id} heartbeat timeout")
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
            setWorlds(request)
            setPlayers(request)
        }
        proxyBridge.servers.add(server)
        heartbeatMap[server] = Instant.now()
        notificationFlow.emit(notification {
            serverRegistration = request
        })
        return serverRegistrationAck {
            ok = true
            servers.addAll(proxyBridge.servers.map { it.toInfo() })
        }
    }

    private fun ProxyRemoteBackendServer.setWorlds(info: ServerInfo) {
        worlds.addAll(info.worldsList.map {
            val world = ProxyRemoteBackendWorld(this, it.name, it.alias)
            val spawnPoint = it.spawnPoint.toImpl(this, world)
            world.apply { this.spawnPoint = spawnPoint }
        })
    }

    private fun ProxyRemoteBackendServer.setPlayers(info: ServerInfo) {
        players.addAll(info.playersList.map {
            val worldName = it.world.name
            val world = getWorld(worldName) ?: error("World not found: $worldName")
            ProxyRemoteBackendPlayer(proxy.getPlayer(it.uniqueId).get(), this, world)
        })
    }

    override suspend fun heartbeat(request: ServerInfo): Empty {
        val remoteServer = proxyBridge.getServer(request.id) as InternalServer?
            ?: error("Server not found: ${request.id}")
        heartbeatMap[remoteServer] = Instant.now()
        remoteServer.isOnline = true
        notificationFlow.emit(notification {
            serverInfoUpdate = request
        })
        return empty
    }

    private suspend fun waitNoReturnAck(request: PlayerOperation): PlayerOperationResult {
        return withTimeoutOrNull(20) {
            for (ack in playerOperationAck) {
                if (ack.uuid != request.id || ack.playerUuid != request.playerUuid) {
                    continue
                }
                when (ack.contentCase!!) {
                    OK -> return@withTimeoutOrNull playerOperationResult {
                        ok = true
                    }

                    UNSUPPORTED -> return@withTimeoutOrNull playerOperationResult {
                        unsupported = true
                    }

                    ContentCase.CONTENT_NOT_SET -> error("Unexpected")
                }
            }
            null
        } ?: return playerOperationResult {
            timeout = true
        }
    }

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        val localPlayer = localServer.getPlayer(request.playerUuid.uuid) ?: return playerOperationResult {
            playerOffline = true
        }
        val remotePlayer = proxyBridge.getRemotePlayer(request.playerUuid.uuid) as ProxyRemoteBackendPlayer?
        when (request.contentCase!!) {
            INFO_LOOKUP -> {
                notificationFlow.emit(notification {
                    playerOperation = request
                })
                return withTimeoutOrNull(20) {
                    for (ack in playerOperationAck) {
                        if (ack.uuid != request.id || ack.playerUuid != request.playerUuid) {
                            continue
                        }
                        return@withTimeoutOrNull when (ack.contentCase!!) {
                            OK -> playerOperationResult {
                                ok = true
                                infoLookup = ack.infoLookup
                            }

                            else -> return@withTimeoutOrNull playerOperationResult {
                                unsupported = true
                            }
                        }
                    }
                    null
                } ?: playerOperationResult {
                    timeout = true
                }
            }

            SEND_MESSAGE -> localPlayer.sendMessage(MiniMessage.miniMessage().deserialize(request.sendMessage))
            SHOW_TITLE -> localPlayer.showTitle {
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
                localPlayer.playSound {
                    val info = request.playSound
                    key(Key.key(info.key))
                    volume(info.volume)
                    pitch(info.pitch)
                }
            }

            TELEPORT -> {
                remotePlayer ?: return playerOperationResult {
                    unsupported = true
                }
                remotePlayer.actual.switchServer(request.teleport.server)
                notificationFlow.emit(notification {
                    playerOperation = request
                })
                return waitNoReturnAck(request)
            }

            PERFORM_COMMAND -> {
                remotePlayer ?: return playerOperationResult {
                    unsupported = true
                }
                notificationFlow.emit(notification {
                    playerOperation = request
                })
                return waitNoReturnAck(request)
            }

            CONTENT_NOT_SET -> error("Received a PlayerOperation without content (id: ${request.id}, player: ${request.playerUuid})")
        }
        return playerOperationResult {
            ok = true
        }
    }

    override suspend fun ackPlayerOperation(request: PlayerOperationAck): Empty {
        playerOperationAck.send(request)
        return empty
    }

    private fun InternalPlayer.update(info: PlayerInfo) {
        world = server.getWorld(info.world.name) ?: error("World not found: ${info.world.name}")
    }

    override suspend fun updatePlayerInfo(request: PlayerInfo): Empty {
        val remotePlayer = proxyBridge.getRemotePlayer(request.uniqueId) as InternalPlayer?
            ?: error("Player not found: ${request.name}")
        remotePlayer.update(request)
        notificationFlow.emit(notification {
            playerInfoUpdate = request
        })
        return empty
    }

    override suspend fun operateWorld(request: WorldOperation): WorldOperationResult {
        return super.operateWorld(request)
    }

    override suspend fun ackWorldOperation(request: WorldOperationAck): Empty {
        return super.ackWorldOperation(request)
    }

    private fun InternalWorld.update(info: WorldInfo) {
        val loc = info.spawnPoint
        spawnPoint = BridgeLocationImpl(server, this, loc.x, loc.y, loc.z, loc.yaw, loc.pitch)
    }

    override suspend fun updateWorldInfo(request: WorldInfo): Empty {
        val remoteServer = proxyBridge.getServer(request.server) ?: error("Server not found: ${request.server}")
        val remoteWorld = remoteServer.getWorld(request.name) as InternalWorld?
            ?: error("World not found: ${request.name}")
        remoteWorld.update(request)
        notificationFlow.emit(notification {
            worldInfoUpdate = request
        })
        return super.updateWorldInfo(request)
    }
}