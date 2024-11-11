package ink.pmc.framework.bridge.proxy

import com.google.protobuf.Empty
import ink.pmc.advkt.sound.key
import ink.pmc.advkt.sound.pitch
import ink.pmc.advkt.sound.volume
import ink.pmc.advkt.title.*
import ink.pmc.framework.FrameworkConfig
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.*
import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineImplBase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation.ContentCase.*
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase.OK
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase.UNSUPPORTED
import ink.pmc.framework.bridge.proxy.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proxy.server.localServer
import ink.pmc.framework.bridge.server.*
import ink.pmc.framework.frameworkLogger
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.player.switchServer
import ink.pmc.framework.utils.player.uuid
import ink.pmc.framework.utils.proto.empty
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withTimeoutOrNull
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.minimessage.MiniMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object BridgeRpc : BridgeRpcCoroutineImplBase(), KoinComponent {
    private val config by lazy { get<FrameworkConfig>().bridge }
    private var isRunning = false
    private val heartbeatMap = ConcurrentHashMap<BridgeServer, Instant>()
    private val heartbeatCheckJob: Job
    private val playerOperationAck = Channel<PlayerOperationAck>()
    private val notificationFlow = MutableSharedFlow<Notification>()

    override fun monitorNotification(request: Empty): Flow<Notification> {
        debugInfo("monitorNotification called")
        return notificationFlow
    }

    suspend fun notify(notification: Notification) {
        notificationFlow.emit(notification)
    }

    private suspend fun checkHeartbeat(server: BridgeServer) {
        if (server.state.isLocal) return
        debugInfo("Check heartbeat: ${server.id}")
        val remoteServer = server as InternalServer
        val time = heartbeatMap[server]
        val requirement = Instant.now().minusSeconds(5)
        if (time == null || time.isBefore(requirement)) {
            remoteServer.isOnline = false
            notificationFlow.emit(notification {
                serverOffline = server.id
            })
            frameworkLogger.warning("Server ${remoteServer.id} heartbeat timeout")
        }
    }

    private suspend fun <T> rpcCatching(block: suspend () -> T): T {
        return runCatching {
            block()
        }.onFailure {
            frameworkLogger.log(Level.SEVERE, "BridgeRpc: Internal error", it)
        }.getOrThrow()
    }

    init {
        isRunning = true
        // Heartbeat check loop
        heartbeatCheckJob = submitAsync {
            while (isRunning) {
                delay(5.seconds)
                internalBridge.servers.forEach {
                    checkHeartbeat(it)
                }
            }
        }
    }

    override suspend fun registerServer(request: ServerInfo): ServerRegistrationResult = rpcCatching {
        debugInfo("registerServer called: $request")
        val server = internalBridge.registerServer(request)
        heartbeatMap[server] = Instant.now()
        notificationFlow.emit(notification {
            serverRegistration = request
        })
        frameworkLogger.info("A server registered successfully: ${request.id} (${request.playersCount} players, ${request.worldsCount} worlds)")
        return@rpcCatching serverRegistrationResult {
            ok = true
            servers.addAll(internalBridge.servers.map { it.createInfo() })
        }
    }

    override suspend fun heartbeat(request: HeartbeatMessage): HeartbeatResult = rpcCatching {
        debugInfo("heartbeat called: $request")
        val remoteServer = internalBridge.getServer(request.server) as InternalServer?
            ?: return@rpcCatching heartbeatResult {
                notRegistered = true
            }
        heartbeatMap[remoteServer] = Instant.now()
        remoteServer.isOnline = true
        notificationFlow.emit(notification {
            serverOnline = request.server
        })
        return@rpcCatching heartbeatResult {
            ok = true
        }
    }

    override suspend fun syncData(request: ServerInfo): DataSyncResult = rpcCatching {
        debugInfo("syncData called: $request")
        val remoteServer = internalBridge.syncData(request)
        heartbeatMap[remoteServer] = Instant.now()
        remoteServer.isOnline = true
        notificationFlow.emit(notification {
            serverInfoUpdate = request
        })
        return@rpcCatching dataSyncResult {
            servers.addAll(internalBridge.servers.map { it.createInfo() })
        }
    }

    private fun handleNoReturnAck(ack: PlayerOperationAck): PlayerOperationResult {
        return when (ack.contentCase!!) {
            OK -> playerOperationResult { ok = true }
            UNSUPPORTED -> playerOperationResult { unsupported = true }
            ContentCase.CONTENT_NOT_SET -> error("Unexpected")
        }
    }

    private suspend fun waitNoReturnAck(request: PlayerOperation): PlayerOperationResult {
        return withTimeoutOrNull(config.operationTimeoutMs) {
            for (ack in playerOperationAck) {
                if (ack.uuid != request.id) continue
                return@withTimeoutOrNull handleNoReturnAck(ack)
            }
            null
        } ?: return playerOperationResult {
            timeout = true
        }
    }

    private fun handleInfoLookupAck(ack: PlayerOperationAck): PlayerOperationResult {
        return when (ack.contentCase!!) {
            OK -> playerOperationResult {
                ok = true
                infoLookup = ack.infoLookup
            }

            else -> playerOperationResult { unsupported = true }
        }
    }

    private suspend fun handleInfoLookup(request: PlayerOperation): PlayerOperationResult {
        notificationFlow.emit(notification {
            playerOperation = request
        })
        return withTimeoutOrNull(config.operationTimeoutMs) {
            for (ack in playerOperationAck) {
                if (ack.uuid != request.id) continue
                return@withTimeoutOrNull handleInfoLookupAck(ack)
            }
            null
        } ?: playerOperationResult { timeout = true }
    }

    private suspend fun handleSendMessage(request: PlayerOperation, player: InternalPlayer): PlayerOperationResult {
        player.sendMessage(MiniMessage.miniMessage().deserialize(request.sendMessage))
        return playerOperationResult { ok = true }
    }

    private suspend fun handleSendTitle(request: PlayerOperation, player: InternalPlayer): PlayerOperationResult {
        player.showTitle {
            val info = request.showTitle
            times {
                fadeIn(info.fadeInMs.milliseconds)
                stay(info.stayMs.milliseconds)
                fadeOut(info.fadeOutMs.milliseconds)
            }
            mainTitle(MiniMessage.miniMessage().deserialize(info.mainTitle))
            subTitle(MiniMessage.miniMessage().deserialize(info.subTitle))
        }
        return playerOperationResult { ok = true }
    }

    private suspend fun handlePlaySound(request: PlayerOperation, player: InternalPlayer): PlayerOperationResult {
        player.playSound {
            val info = request.playSound
            key(Key.key(info.key))
            volume(info.volume)
            pitch(info.pitch)
        }
        return playerOperationResult { ok = true }
    }

    private fun PlayerOperation.getRemotePlayer(): InternalPlayer? {
        return internalBridge.getRemotePlayer(playerUuid.uuid) as InternalPlayer?
    }

    private suspend fun handleTeleport(request: PlayerOperation): PlayerOperationResult {
        val remotePlayer = request.getRemotePlayer() as ProxyRemoteBackendPlayer? ?: return playerOperationResult {
            unsupported = true
        }
        remotePlayer.actual.switchServer(request.teleport.server)
        notificationFlow.emit(notification { playerOperation = request })
        return waitNoReturnAck(request)
    }

    private suspend fun handlePerformCommand(request: PlayerOperation): PlayerOperationResult {
        request.getRemotePlayer() ?: return playerOperationResult { unsupported = true }
        notificationFlow.emit(notification { playerOperation = request })
        return waitNoReturnAck(request)
    }

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult = rpcCatching {
        debugInfo("operatePlayer called: $request")
        val localPlayer = localServer.getPlayer(request.playerUuid.uuid, ServerState.LOCAL, ServerType.PROXY)
                as InternalPlayer? ?: return@rpcCatching playerOperationResult { playerOffline = true }
        return@rpcCatching when (request.contentCase!!) {
            INFO_LOOKUP -> handleInfoLookup(request)
            SEND_MESSAGE -> handleSendMessage(request, localPlayer)
            SHOW_TITLE -> handleSendTitle(request, localPlayer)
            PLAY_SOUND -> handlePlaySound(request, localPlayer)
            TELEPORT -> handleTeleport(request)
            PERFORM_COMMAND -> handlePerformCommand(request)
            CONTENT_NOT_SET -> error("Received a PlayerOperation without content (id: ${request.id}, player: ${request.playerUuid})")
        }
    }

    override suspend fun ackPlayerOperation(request: PlayerOperationAck): Empty = rpcCatching {
        debugInfo("ackPlayerOperation called: $request")
        playerOperationAck.send(request)
        return@rpcCatching empty
    }

    override suspend fun updatePlayerInfo(request: PlayerInfo): Empty = rpcCatching {
        internalBridge.updateRemotePlayerInfo(request)
        notificationFlow.emit(notification { playerInfoUpdate = request })
        return@rpcCatching empty
    }

    override suspend fun operateWorld(request: WorldOperation): WorldOperationResult = rpcCatching {
        debugInfo("operateWorld called: $request")
        error("Placeholder")
    }

    override suspend fun ackWorldOperation(request: WorldOperationAck): Empty = rpcCatching {
        debugInfo("ackWorldOperation called: $request")
        error("Placeholder")
    }

    override suspend fun loadWorld(request: WorldInfo): Empty = rpcCatching {
        debugInfo("loadWorld called: $request")
        internalBridge.addRemoteWorld(request)
        notificationFlow.emit(notification {
            worldLoad = request
        })
        return@rpcCatching empty
    }

    override suspend fun updateWorldInfo(request: WorldInfo): Empty = rpcCatching {
        debugInfo("updateRemoteWorldInfo called: $request")
        internalBridge.updateRemoteWorldInfo(request)
        notificationFlow.emit(notification { worldInfoUpdate = request })
        return@rpcCatching empty
    }

    override suspend fun unloadWorld(request: WorldLoad): Empty = rpcCatching {
        debugInfo("unloadWorld called: $request")
        internalBridge.removeRemoteWorld(request)
        notificationFlow.emit(notification { worldUnload = request })
        return@rpcCatching empty
    }
}