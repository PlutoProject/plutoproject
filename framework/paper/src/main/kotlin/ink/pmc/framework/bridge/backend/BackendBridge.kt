package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import org.koin.java.KoinJavaComponent.getKoin

internal val backendBridge: BackendBridge
    get() = getKoin().get<Bridge>() as BackendBridge

class BackendBridge : Bridge {
    override val local: BridgeServer
        get() = TODO("Not yet implemented")
    override val groups: Collection<BridgeGroup>
        get() = TODO("Not yet implemented")

    override fun getGroup(id: String): BridgeGroup? {
        TODO("Not yet implemented")
    }

    override fun isGroupRegistered(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override val servers: Collection<BridgeServer>
        get() = TODO("Not yet implemented")
}