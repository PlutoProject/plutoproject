package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.backend.player.BackendRemoteBackendPlayer
import ink.pmc.framework.bridge.backend.player.BackendRemoteProxyPlayer
import ink.pmc.framework.bridge.backend.server.BackendLocalServer
import ink.pmc.framework.bridge.backend.server.BackendRemoteProxyServer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.ServerRegistrationResult.StateCase.*
import ink.pmc.framework.bridge.server.BridgeGroupImpl
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.bridge.server.RemoteBackendServer
import ink.pmc.framework.bridge.server.toInfo
import ink.pmc.framework.bridge.world.BridgeLocationImpl
import ink.pmc.framework.bridge.world.RemoteBackendWorld
import ink.pmc.framework.utils.data.mutableConcurrentListOf
import ink.pmc.framework.utils.player.uuid
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.getKoin

internal val backendBridge: BackendBridge
    get() = getKoin().get<Bridge>() as BackendBridge

class BackendBridge : Bridge {
    override val local: BridgeServer = BackendLocalServer()
    override val servers: MutableList<BridgeServer> = mutableConcurrentListOf(local)

    init {
        runBlocking {
            val result = bridgeStub.registerServer(local.toInfo())
            when (result.stateCase!!) {
                OK -> {
                    servers.addAll(result.serversList.map { server ->
                        if (server.proxy) {
                            BackendRemoteProxyServer().apply {
                                players.addAll(server.playersList.map {
                                    BackendRemoteProxyPlayer(it.uniqueId.uuid, it.name)
                                })
                            }
                        } else {
                            val group = getGroup(server.group) ?: BridgeGroupImpl(server.group)
                            RemoteBackendServer(server.id, group).apply {
                                worlds.addAll(server.worldsList.map {
                                    val backendServer = this
                                    val info = it.spawnPoint
                                    RemoteBackendWorld(this, it.name, it.alias).apply {
                                        spawnPoint = BridgeLocationImpl(
                                            backendServer, this, info.x, info.y, info.z, info.yaw, info.pitch
                                        )
                                    }
                                })
                                players.addAll(server.playersList.map {
                                    BackendRemoteBackendPlayer(it.uniqueId.uuid, it.name, this, getWorld(it.world.name))
                                })
                            }
                        }
                    })
                }

                ID_EXISTED -> error("Server id existed on master")
                STATE_NOT_SET -> error("Received a ServerRegistrationResult without state")
            }
        }
    }
}