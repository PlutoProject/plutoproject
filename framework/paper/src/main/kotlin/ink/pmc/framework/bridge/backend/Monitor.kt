package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.backend.player.BackendRemoteBackendPlayer
import ink.pmc.framework.bridge.backend.player.BackendRemoteProxyPlayer
import ink.pmc.framework.bridge.backend.server.localServer
import ink.pmc.framework.bridge.backend.world.getBridge
import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.player.createInfo
import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineStub
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification.ContentCase.*
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification.ContentCase.CONTENT_NOT_SET
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation.ContentCase.*
import ink.pmc.framework.bridge.proto.playerOperationAck
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.bridge.world.InternalWorld
import ink.pmc.framework.bridge.world.RemoteBackendWorld
import ink.pmc.framework.bridge.world.createBridge
import ink.pmc.framework.frameworkLogger
import ink.pmc.framework.rpc.RpcClient
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.data.mutableConcurrentSetOf
import ink.pmc.framework.utils.player.uuid
import ink.pmc.framework.utils.proto.empty
import io.grpc.StatusException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.*
import kotlin.time.Duration.Companion.seconds

val bridgeStub = BridgeRpcCoroutineStub(RpcClient.channel)
private var monitorJob: Job? = null
internal val operationsSent = mutableConcurrentSetOf<UUID>()

fun startBridgeMonitor() {
    monitorJob = submitAsync {
        while (true) {
            try {
                bridgeStub.monitorNotification(empty)
                    .also { frameworkLogger.info("Connecting Bridge monitor") }
                    .collect {
                        handleNotification(it)
                    }
            } catch (e: StatusException) {
                frameworkLogger.severe("Bridge monitor disconnected, wait 10s before retry")
                delay(10.seconds)
            }
        }
    }
}

fun stopBridgeMonitor() {
    monitorJob?.cancel()
    monitorJob = null
}

private fun getRemoteServer(id: String): InternalServer? {
    if (localServer.id == id) return null
    return backendBridge.getServer(id) as InternalServer?
}

private fun getRemotePlayer(uuid: UUID): InternalPlayer? {
    return backendBridge.getRemotePlayer(uuid) as InternalPlayer?
}

private fun InternalServer.getRemoteWorld(name: String): InternalWorld? {
    return getWorld(name) as InternalWorld?
}

private suspend fun handlePlayerOperation(msg: PlayerOperation) {
    if (operationsSent.remove(msg.id.uuid)) return
    val localPlayer = localServer.getPlayer(msg.playerUuid.uuid) ?: error("Player not found: ${msg.playerUuid}")
    when (msg.contentCase!!) {
        INFO_LOOKUP -> {
            bridgeStub.ackPlayerOperation(playerOperationAck {
                ok = true
                infoLookup = localPlayer.createInfo()
            })
            return
        }

        SEND_MESSAGE -> error("Unexpected")
        SHOW_TITLE -> error("Unexpected")
        PLAY_SOUND -> error("Unexpected")
        TELEPORT -> {
            val location = localServer.getWorld(msg.teleport.world)?.getLocation(
                msg.teleport.x,
                msg.teleport.y,
                msg.teleport.z,
                msg.teleport.yaw,
                msg.teleport.pitch,
            ) ?: error("World not found: ${msg.teleport.world} (server: ${msg.teleport.server})")
            localPlayer.teleport(location)
        }

        PERFORM_COMMAND -> localPlayer.performCommand(msg.performCommand)
        PlayerOperation.ContentCase.CONTENT_NOT_SET -> error("Received a PlayerOperation without content")
    }
    bridgeStub.ackPlayerOperation(playerOperationAck {
        ok = true
    })
}

private fun InternalPlayer.update(info: PlayerInfo) {
    world = info.world.getBridge() ?: error("World not found: ${info.world.name} (server: ${info.world.name})")
}

private fun handlePlayerInfoUpdate(info: PlayerInfo) {
    // 加入本服的处理在事件
    if (info.server == localServer.id) return
    val remotePlayer = getRemotePlayer(info.uniqueId.uuid)
    val remoteServer = getRemoteServer(info.server) ?: error("Server not found: ${info.server}")
    // 玩家在整个网络中不存在
    if (remotePlayer == null) {
        val new = if (info.proxy) {
            BackendRemoteProxyPlayer(info.uniqueId.uuid, info.name)
        } else {
            val remoteWorld = remoteServer.getWorld(info.world.name)
                ?: error("World not found: ${info.world.name} (server: ${info.server})")
            BackendRemoteBackendPlayer(info.uniqueId.uuid, info.name, remoteServer, remoteWorld)
        }
        remoteServer.players.add(new)
        return
    }
    // 玩家进入第一个后端服务器
    if (remotePlayer.serverType.isProxy && info.backend) {
        val backendServer = getRemoteServer(info.server) ?: error("Server not found: ${info.server}")
        val backendWorld = backendServer.getRemoteWorld(info.world.name)
            ?: error("World not found: ${info.world.name} (server: ${info.server})")
        val new = BackendRemoteBackendPlayer(info.uniqueId.uuid, info.name, backendServer, backendWorld)
        backendServer.players.add(new)
        return
    }
    // 玩家切换后端服务器
    if (remotePlayer.serverType.isBackend && info.server != remoteServer.id) {
        val newServer = getRemoteServer(info.server) ?: error("Server not found: ${info.server}")
        remotePlayer.world = null
        remotePlayer.server = newServer
        newServer.players.add(remotePlayer)
        remoteServer.players.remove(remotePlayer)
        return
    }
    remotePlayer.update(info)
}

private fun handlePlayerDisconnect(msg: PlayerDisconnect) {
    if (msg.server == localServer.id) return
    val uuid = msg.uniqueId.uuid
    val remoteProxy = backendBridge.getPlayer(uuid, ServerState.REMOTE, ServerType.PROXY) as InternalPlayer?
    val remoteBackend = backendBridge.getPlayer(uuid, ServerState.REMOTE, ServerType.BACKEND) as InternalPlayer?
    if (remoteProxy == null && remoteBackend == null) error("Player not found: $uuid")
    remoteProxy?.apply {
        isOnline = false
        (server as InternalServer).players.remove(this)
    }
    remoteBackend?.apply {
        isOnline = false
        (server as InternalServer).players.remove(this)
    }
}

private fun handleWorldOperation(msg: WorldOperation) {
    if (msg.server == localServer.id) return
    error("Placeholder")
}

private fun InternalWorld.update(info: WorldInfo) {
    spawnPoint = info.spawnPoint.createBridge()
}

private fun handleWorldInfoUpdate(msg: WorldInfo) {
    if (msg.server == localServer.id) return
    val remoteServer = getRemoteServer(msg.server) ?: error("Server not found: ${msg.server}")
    val remoteWorld = remoteServer.getWorld(msg.name) as InternalWorld?
    // 加载世界
    if (remoteWorld == null) {
        val new = RemoteBackendWorld(remoteServer, msg.name, msg.alias)
        remoteServer.worlds.add(new)
        return
    }
    remoteWorld.update(msg)
}

private fun handleWorldUnload(msg: WorldLoad) {
    if (msg.server == localServer.id) return
    val remoteServer = getRemoteServer(msg.server) ?: error("Server not found: ${msg.server}")
    val remoteWorld = remoteServer.getRemoteWorld(msg.world)
        ?: error("World not found: ${msg.world} (server: ${msg.server})")
    remoteServer.worlds.remove(remoteWorld)
}

private suspend fun handleNotification(msg: Notification) {
    when (msg.contentCase!!) {
        SERVER_REGISTRATION -> backendBridge.addServer(msg.serverRegistration)
        SERVER_INFO_UPDATE -> getRemoteServer(msg.serverInfoUpdate.id)?.isOnline = true
        SERVER_OFFLINE -> getRemoteServer(msg.serverOffline)?.isOnline = false
        PLAYER_OPERATION -> handlePlayerOperation(msg.playerOperation)
        PLAYER_INFO_UPDATE -> handlePlayerInfoUpdate(msg.playerInfoUpdate)
        PLAYER_DISCONNECT -> handlePlayerDisconnect(msg.playerDisconnect)
        WORLD_OPERATION -> handleWorldOperation(msg.worldOperation)
        WORLD_INFO_UPDATE -> handleWorldInfoUpdate(msg.worldInfoUpdate)
        WORLD_UNLOAD -> handleWorldUnload(msg.worldUnload)
        CONTENT_NOT_SET -> error("Received a Notification without content")
    }
}