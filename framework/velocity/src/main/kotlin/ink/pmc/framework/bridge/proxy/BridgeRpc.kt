package ink.pmc.framework.bridge.proxy

import com.google.protobuf.Empty
import ink.pmc.advkt.sound.key
import ink.pmc.advkt.sound.pitch
import ink.pmc.advkt.sound.volume
import ink.pmc.advkt.title.*
import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineImplBase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation.ContentCase.*
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase.OK
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationAck.ContentCase.UNSUPPORTED
import ink.pmc.framework.bridge.proto.notification
import ink.pmc.framework.bridge.proto.playerOperationResult
import ink.pmc.framework.bridge.proto.serverRegistrationResult
import ink.pmc.framework.bridge.proxy.player.ProxyRemoteBackendPlayer
import ink.pmc.framework.bridge.proxy.server.localServer
import ink.pmc.framework.bridge.server.*
import ink.pmc.framework.bridge.update
import ink.pmc.framework.bridge.world.BridgeLocationImpl
import ink.pmc.framework.bridge.world.InternalWorld
import ink.pmc.framework.bridge.world.RemoteBackendWorld
import ink.pmc.framework.bridge.world.createBridge
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

    private suspend fun handleHeartbeat(server: BridgeServer) {
        if (server.state.isLocal) return
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

    init {
        isRunning = true
        // Heartbeat check loop
        heartbeatCheckJob = submitAsync {
            while (isRunning) {
                delay(5.seconds)
                proxyBridge.servers.forEach {
                    handleHeartbeat(it)
                }
            }
        }
    }

    override suspend fun registerServer(request: ServerInfo): ServerRegistrationResult {
        if (Bridge.isServerRegistered(request.id)) {
            return serverRegistrationResult {
                idExisted = true
            }
        }
        val id = request.id
        val group = request.group?.let { proxyBridge.getGroup(it) ?: BridgeGroupImpl(it) }
        val server = RemoteBackendServer(id, group).apply {
            setWorlds(request)
            setPlayers(request)
        }
        proxyBridge.servers.add(server)
        heartbeatMap[server] = Instant.now()
        notificationFlow.emit(notification {
            serverRegistration = request
        })
        return serverRegistrationResult {
            ok = true
            servers.addAll(proxyBridge.servers.map { it.createInfo() })
        }
    }

    private fun RemoteBackendServer.setWorlds(info: ServerInfo) {
        worlds.clear()
        worlds.addAll(info.worldsList.map {
            val world = RemoteBackendWorld(this, it.name, it.alias)
            val spawnPoint = it.spawnPoint.createBridge(this, world)
            world.apply { this.spawnPoint = spawnPoint }
        })
    }

    private fun RemoteBackendServer.setPlayers(info: ServerInfo) {
        players.clear()
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

    private fun handleNoReturnAck(ack: PlayerOperationAck): PlayerOperationResult {
        return when (ack.contentCase!!) {
            OK -> playerOperationResult { ok = true }
            UNSUPPORTED -> playerOperationResult { unsupported = true }
            ContentCase.CONTENT_NOT_SET -> error("Unexpected")
        }
    }

    private suspend fun waitNoReturnAck(request: PlayerOperation): PlayerOperationResult {
        return withTimeoutOrNull(20) {
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
        return withTimeoutOrNull(20) {
            for (ack in playerOperationAck) {
                if (ack.uuid != request.id) continue
                return@withTimeoutOrNull handleInfoLookupAck(ack)
            }
            null
        } ?: playerOperationResult {
            timeout = true
        }
    }

    private suspend fun handleSendMessage(request: PlayerOperation, player: InternalPlayer): PlayerOperationResult {
        player.sendMessage(MiniMessage.miniMessage().deserialize(request.sendMessage))
        return playerOperationResult { ok = true }
    }

    private suspend fun handleSendTitle(request: PlayerOperation, player: InternalPlayer): PlayerOperationResult {
        player.showTitle {
            val info = request.showTitle
            times {
                fadeIn(info.fadeIn.ticks)
                stay(info.stay.ticks)
                fadeOut(info.fadeOut.ticks)
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
        return proxyBridge.getRemotePlayer(playerUuid.uuid) as InternalPlayer?
    }

    private suspend fun handleTeleport(request: PlayerOperation): PlayerOperationResult {
        val remotePlayer = request.getRemotePlayer() as ProxyRemoteBackendPlayer?
            ?: return playerOperationResult { unsupported = true }
        remotePlayer.actual.switchServer(request.teleport.server)
        notificationFlow.emit(notification { playerOperation = request })
        return waitNoReturnAck(request)
    }

    private suspend fun handlePerformCommand(request: PlayerOperation): PlayerOperationResult {
        request.getRemotePlayer()
            ?: return playerOperationResult { unsupported = true }
        notificationFlow.emit(notification { playerOperation = request })
        return waitNoReturnAck(request)
    }

    override suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult {
        val localPlayer = localServer.getPlayer(request.playerUuid.uuid) as InternalPlayer?
            ?: return playerOperationResult { playerOffline = true }
        return when (request.contentCase!!) {
            INFO_LOOKUP -> handleInfoLookup(request)
            SEND_MESSAGE -> handleSendMessage(request, localPlayer)
            SHOW_TITLE -> handleSendTitle(request, localPlayer)
            PLAY_SOUND -> handlePlaySound(request, localPlayer)
            TELEPORT -> handleTeleport(request)
            PERFORM_COMMAND -> handlePerformCommand(request)
            CONTENT_NOT_SET -> error("Received a PlayerOperation without content (id: ${request.id}, player: ${request.playerUuid})")
        }
    }

    override suspend fun ackPlayerOperation(request: PlayerOperationAck): Empty {
        playerOperationAck.send(request)
        return empty
    }

    override suspend fun updatePlayerInfo(request: PlayerInfo): Empty {
        val remotePlayer = proxyBridge.getRemotePlayer(request.uniqueId) as InternalPlayer?
            ?: error("Player not found: ${request.name}")
        remotePlayer.update(request)
        notificationFlow.emit(notification { playerInfoUpdate = request })
        return empty
    }

    override suspend fun operateWorld(request: WorldOperation): WorldOperationResult {
        error("Placeholder")
    }

    override suspend fun ackWorldOperation(request: WorldOperationAck): Empty {
        error("Placeholder")
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
        notificationFlow.emit(notification { worldInfoUpdate = request })
        return super.updateWorldInfo(request)
    }

    override suspend fun unloadWorld(request: WorldLoad): Empty {
        val remoteServer = proxyBridge.getServer(request.server) as InternalServer?
            ?: error("Server not found: ${request.server}")
        val remoteWorld = remoteServer.getWorld(request.world) ?: error("World not found: ${request.world}")
        remoteServer.worlds.remove(remoteWorld)
        notificationFlow.emit(notification { worldUnload = request })
        return empty
    }
}