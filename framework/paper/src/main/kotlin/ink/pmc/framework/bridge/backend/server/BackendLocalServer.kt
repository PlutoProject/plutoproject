package ink.pmc.framework.bridge.backend.server

import ink.pmc.framework.FrameworkConfig
import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.backend.player.BackendLocalPlayer
import ink.pmc.framework.bridge.backend.world.BackendLocalWorld
import ink.pmc.framework.bridge.server.*
import ink.pmc.framework.utils.platform.paper
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal inline val localServer: BackendLocalServer
    get() = Bridge.local as BackendLocalServer

class BackendLocalServer : InternalServer(), KoinComponent {
    private val config by lazy { get<FrameworkConfig>().bridge }
    override val id: String = config.id
    override val group: BridgeGroup? = config.group?.let { BridgeGroupImpl(it) }
    override val type: ServerType = ServerType.BACKEND
    override val state: ServerState = ServerState.LOCAL
    override var isOnline: Boolean = true
        set(_) = error("Unsupported")

    init {
        worlds.addAll(paper.worlds.map { BackendLocalWorld(it) })
        players.addAll(paper.onlinePlayers.map { BackendLocalPlayer(it) })
    }
}