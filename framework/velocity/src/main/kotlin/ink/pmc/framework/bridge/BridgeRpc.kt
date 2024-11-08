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
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase
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
            val world = getWorld(it.location.world) ?: error("World not found: ${it.location.world}")
            ProxyRemoteBackendPlayer(proxy.getPlayer(it.uniqueId).get(), this, world)
        })
    }

    override suspend fun heartbeat(request: ServerInfo): Empty {
        val server = proxyBridge.getServer(request.id) as ProxyRemoteBackendServer? ?: return empty
        heartbeatMap[server] = Instant.now()
        server.isOnline = true
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
        val local = localServer.getPlayer(request.playerUuid.uuid) ?: return playerOperationResult {
            playerOffline = true
        }
        val nonLocal = proxyBridge.getNonLocalPlayer(request.playerUuid.uuid) as ProxyRemoteBackendPlayer?
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

            SEND_MESSAGE -> local.sendMessage(MiniMessage.miniMessage().deserialize(request.sendMessage))
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
                nonLocal ?: return playerOperationResult {
                    unsupported = true
                }
                nonLocal.actual.switchServer(request.teleport.server)
                notificationFlow.emit(notification {
                    playerOperation = request
                })
                return waitNoReturnAck(request)
            }

            PERFORM_COMMAND -> {
                nonLocal ?: return playerOperationResult {
                    unsupported = true
                }
                notificationFlow.emit(notification {
                    playerOperation = request
                })
                return waitNoReturnAck(request)
            }

            CONTENT_NOT_SET -> error("Received a operation request with no content: ${request.id}")
        }
        return playerOperationResult {
            ok = true
        }
    }

    override suspend fun ackPlayerOperation(request: PlayerOperationAck): Empty {
        playerOperationAck.send(request)
        return empty
    }

    override suspend fun operateWorld(request: WorldOperation): WorldOperationAck {
        return super.operateWorld(request)
    }

    override suspend fun ackWorldOperation(request: WorldOperationAck): Empty {
        return super.ackWorldOperation(request)
    }
}