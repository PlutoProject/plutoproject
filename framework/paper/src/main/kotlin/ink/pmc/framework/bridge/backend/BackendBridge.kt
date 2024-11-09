package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.backend.server.BackendLocalServer
import ink.pmc.framework.bridge.server.BridgeServer
import ink.pmc.framework.utils.data.mutableConcurrentListOf
import org.koin.java.KoinJavaComponent.getKoin

internal val backendBridge: BackendBridge
    get() = getKoin().get<Bridge>() as BackendBridge

class BackendBridge : Bridge {
    override val local: BridgeServer = BackendLocalServer()
    override val servers: Collection<BridgeServer> = mutableConcurrentListOf(local)
}