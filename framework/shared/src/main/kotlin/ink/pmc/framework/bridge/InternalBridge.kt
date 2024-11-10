package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*
import ink.pmc.framework.bridge.server.BridgeGroupImpl
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.InternalServer
import ink.pmc.framework.bridge.server.RemoteBackendServer
import ink.pmc.framework.bridge.world.InternalWorld
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

    abstract fun createWorld(info: WorldInfo, server: InternalServer? = null): InternalWorld

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
        require(id != local.id) { "Server ID conflict with local server" }
        if (isServerRegistered(id)) {
            unregisterServer(id)
        }
        val server = createServer(info)
        servers.add(server)
        return server
    }

    fun syncData(info: ServerInfo): InternalServer {
        val remoteServer = getServer(info.id) as InternalServer? ?: error("Server not found: ${info.id}")
        remoteServer.players.forEach { (it as InternalPlayer).isOnline = false }
        remoteServer.setInitialPlayers(info, remoteServer)
        remoteServer.setInitialWorlds(info, remoteServer)
        return remoteServer
    }

    fun addPlayer(info: PlayerInfo): InternalPlayer {
        val remotePlayer = createPlayer(info)
        (remotePlayer.server as InternalServer).players.add(remotePlayer)
        return remotePlayer
    }

    fun updatePlayerInfo(info: PlayerInfo): InternalPlayer {
        val remotePlayer = getRemotePlayer(info.uniqueId.uuid) as InternalPlayer?
            ?: error("Player not found: ${info.uniqueId}")
        remotePlayer.world = getWorld(remotePlayer.server, info.world.name)
            ?: error("World not found: ${info.world.name} (server: ${remotePlayer.server.id})")
        return remotePlayer
    }

    fun removePlayer(uuid: UUID) {
        val remotePlayer = getRemotePlayer(uuid) as InternalPlayer? ?: error("Player not found: $uuid")
        remotePlayer.isOnline = false
        (remotePlayer.server as InternalServer).players.remove(remotePlayer)
    }

    fun addWorld(info: WorldInfo): InternalWorld {
        val remoteWorld = createWorld(info)
        (remoteWorld.server as InternalServer).worlds.add(remoteWorld)
        return remoteWorld
    }

    fun updateWorldInfo(info: WorldInfo): InternalWorld {
        val remoteServer = getServer(info.server) as InternalServer?
            ?: error("Server not found: ${info.server}")
        val remoteWorld = remoteServer.getWorld(info.name) as InternalWorld?
            ?: error("World not found: ${info.name} (server: ${remoteServer.id})")
        remoteWorld.spawnPoint = info.spawnPoint.createBridge()
        return remoteWorld
    }

    fun removeWorld(load: WorldLoad) {
        val remoteServer = getServer(load.server) as InternalServer? ?: error("Server not found: ${load.server}")
        val remoteWorld = remoteServer.getWorld(load.world) as InternalWorld?
            ?: error("World not found: ${load.world} (server: ${load.server})")
        remoteServer.worlds.remove(remoteWorld)
    }
}