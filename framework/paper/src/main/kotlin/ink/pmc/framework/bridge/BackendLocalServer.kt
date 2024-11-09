package ink.pmc.framework.bridge

import ink.pmc.framework.FrameworkConfig
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class BackendLocalServer : InternalServer(), KoinComponent {
    private val config by lazy { get<FrameworkConfig>().bridge }
    override val id: String = config.id
    override val group: BridgeGroup?
        get() = TODO("Not yet implemented")
    override val type: ServerType = ServerType.BACKEND
    override val state: ServerState = ServerState.LOCAL
    override var isOnline: Boolean = true
        set(_) = error("Unsupported")
}