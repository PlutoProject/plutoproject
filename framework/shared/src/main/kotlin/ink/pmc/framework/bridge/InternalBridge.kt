package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*
import ink.pmc.framework.bridge.server.*
import ink.pmc.framework.bridge.world.InternalWorld
import ink.pmc.framework.bridge.world.RemoteBackendWorld
import ink.pmc.framework.bridge.world.createBridge
import ink.pmc.framework.utils.player.uuid
import org.koin.java.KoinJavaComponent.getKoin
import java.util.*

val internalBridge: InternalBridge
    get() = getKoin().get<Bridge>() as InternalBridge

abstract class InternalBridge : Bridge {
    override val servers: MutableSet<BridgeServer> = mutableSetOf()

    private fun unregisterServer(id: String) {
        require(id != local.id) { "Cannot unregister local server" }
        val remoteServer = getServer(id) as InternalServer? ?: error("Server not found: $id")
        remoteServer.isOnline = false
        remoteServer.players.forEach { (it as InternalPlayer).isOnline = false }
        remoteServer.players.clear()
        remoteServer.worlds.clear()
        servers.remove(remoteServer)
    }

    abstract fun createPlayer(info: PlayerInfo, server: InternalServer? = null): InternalPlayer

    fun createWorld(info: WorldInfo, server: InternalServer? = null): InternalWorld {
        val actualServer = server ?: getInternalServer(info.server)
        return RemoteBackendWorld(actualServer, info.name, info.alias)
    }

    fun getInternalServer(id: String): InternalServer {
        return getServer(id) as InternalServer? ?: error("Server not found: $id")
    }

    // 在后端上，具有同一个 UUID 的 RemotePlayer 实例通常不止一个
    // 一个远程代理端实例，一个远程后端实例
    fun getInternalRemotePlayer(uniqueId: UUID): InternalPlayer {
        return getRemotePlayer(uniqueId) as InternalPlayer? ?: error("Player not found: $uniqueId")
    }

    fun getInternalRemoteBackendPlayer(uniqueId: UUID): InternalPlayer {
        return getPlayer(uniqueId, ServerState.REMOTE, ServerType.BACKEND) as InternalPlayer?
            ?: error("Player not found: $uniqueId")
    }

    fun getInternalWorld(server: BridgeServer, name: String): InternalWorld {
        return server.getWorld(name) as InternalWorld? ?: error("World not found: $name (server: ${server.id})")
    }

    private fun InternalServer.setInitialPlayers(info: ServerInfo, server: InternalServer) {
        players.clear()
        players.addAll(info.playersList.map {
            createPlayer(it, server)
        })
    }

    private fun InternalServer.setInitialWorlds(info: ServerInfo, server: InternalServer) {
        worlds.clear()
        worlds.addAll(info.worldsList.map {
            createWorld(it, server)
        })
    }

    private fun createServer(info: ServerInfo): InternalServer {
        val id = info.id
        val group = info.group?.let { getGroup(it) ?: BridgeGroupImpl(it) }
        val server = RemoteBackendServer(id, group).apply {
            setInitialPlayers(info, this)
            setInitialWorlds(info, this)
        }
        return server
    }

    fun registerServer(info: ServerInfo): BridgeServer {
        val id = info.id
        if (isServerRegistered(id)) {
            unregisterServer(id)
        }
        val server = createServer(info)
        servers.add(server)
        return server
    }

    fun markServerOffline(id: String) {
        val remoteServer = getInternalServer(id)
        remoteServer.isOnline = false
        remoteServer.players.forEach { (it as InternalPlayer).isOnline = false }
        remoteServer.players.clear()
        remoteServer.worlds.clear()
    }

    fun markServerOnline(id: String) {
        val remoteServer = getInternalServer(id)
        remoteServer.isOnline = true
    }

    fun syncData(info: ServerInfo): InternalServer {
        val remoteServer = getInternalServer(info.id)
        remoteServer.players.forEach { (it as InternalPlayer).isOnline = false }
        remoteServer.setInitialPlayers(info, remoteServer)
        remoteServer.setInitialWorlds(info, remoteServer)
        return remoteServer
    }

    fun addRemotePlayer(info: PlayerInfo): InternalPlayer {
        val remotePlayer = createPlayer(info)
        (remotePlayer.server as InternalServer).players.add(remotePlayer)
        return remotePlayer
    }

    fun updatePlayerInfo(info: PlayerInfo): InternalPlayer {
        val remotePlayer = getInternalRemoteBackendPlayer(info.uniqueId.uuid)
        remotePlayer.world = getInternalWorld(remotePlayer.server, info.world.name)
        return remotePlayer
    }

    fun remotePlayerSwitchServer(info: PlayerSwitchServer) {
        val remotePlayer = getInternalRemoteBackendPlayer(info.playerUuid.uuid)
        val target = getInternalServer(info.server)
        target.players.add(remotePlayer)
        remotePlayer.server = target
        remotePlayer.world = null
        (remotePlayer.server as InternalServer).players.remove(remotePlayer)
    }

    fun removeRemotePlayers(uuid: UUID) {
        servers.flatMap { it.players }.filter { it.uniqueId == uuid }.forEach {
            it as InternalPlayer
            it.isOnline = false
            (it.server as InternalServer).players.remove(it)
        }
    }

    fun removeRemoteBackendPlayer(uuid: UUID) {
        val remotePlayer = getInternalRemoteBackendPlayer(uuid)
        remotePlayer.isOnline = false
        (remotePlayer.server as InternalServer).players.remove(remotePlayer)
    }

    fun addRemoteWorld(info: WorldInfo): InternalWorld {
        val remoteWorld = createWorld(info)
        (remoteWorld.server as InternalServer).worlds.add(remoteWorld)
        return remoteWorld
    }

    fun updateWorldInfo(info: WorldInfo): InternalWorld {
        val remoteServer = getInternalServer(info.server)
        val remoteWorld = getInternalWorld(remoteServer, info.name)
        remoteWorld.spawnPoint = info.spawnPoint.createBridge()
        return remoteWorld
    }

    fun removeRemoteWorld(load: WorldLoad) {
        val remoteServer = getInternalServer(load.server)
        val remoteWorld = getInternalWorld(remoteServer, load.world)
        remoteServer.worlds.remove(remoteWorld)
    }
}