package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.backend.player.BackendRemoteBackendPlayer
import ink.pmc.framework.bridge.backend.player.BackendRemoteProxyPlayer
import ink.pmc.framework.bridge.backend.server.BackendLocalServer
import ink.pmc.framework.bridge.backend.server.BackendRemoteProxyServer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.ServerInfo
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.ServerRegistrationResult
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.ServerRegistrationResult.StateCase.*
import ink.pmc.framework.bridge.server.*
import ink.pmc.framework.bridge.world.RemoteBackendWorld
import ink.pmc.framework.bridge.world.toImpl
import ink.pmc.framework.utils.data.mutableConcurrentListOf
import ink.pmc.framework.utils.player.uuid
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.getKoin

internal val backendBridge: BackendBridge
    get() = getKoin().get<Bridge>() as BackendBridge

class BackendBridge : Bridge {
    override val local: BridgeServer = BackendLocalServer()
    override val servers: MutableList<BridgeServer> = mutableConcurrentListOf(local)

    private fun setServers(result: ServerRegistrationResult) {
        result.serversList.forEach { addServer(it) }
    }

    fun addServer(server: ServerInfo) {
        servers.add(if (server.proxy) {
            BackendRemoteProxyServer().apply { setProxyPlayers(server) }
        } else {
            val group = getGroup(server.group) ?: BridgeGroupImpl(server.group)
            RemoteBackendServer(server.id, group).apply {
                setWorlds(server)
                setRemotePlayers(server)
            }
        })
    }

    private fun InternalServer.setWorlds(info: ServerInfo) {
        worlds.addAll(info.worldsList.map {
            val world = RemoteBackendWorld(this, it.name, it.alias)
            val spawnPoint = it.spawnPoint.toImpl(this, world)
            world.apply { this.spawnPoint = spawnPoint }
        })
    }

    private fun InternalServer.setProxyPlayers(info: ServerInfo) {
        players.addAll(info.playersList.map {
            BackendRemoteProxyPlayer(it.uniqueId.uuid, it.name)
        })
    }

    private fun InternalServer.setRemotePlayers(info: ServerInfo) {
        players.addAll(info.playersList.map {
            val worldName = it.world.name
            val world = getWorld(worldName) ?: error("World not found: $worldName")
            BackendRemoteBackendPlayer(it.uniqueId.uuid, it.name, this, world)
        })
    }

    init {
        runBlocking {
            val result = bridgeStub.registerServer(local.toInfo())
            when (result.stateCase!!) {
                OK -> setServers(result)
                ID_EXISTED -> error("Server id existed on master")
                STATE_NOT_SET -> error("Received a ServerRegistrationResult without state")
            }
        }
    }
}