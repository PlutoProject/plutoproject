package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.server.BridgeServer
import org.koin.java.KoinJavaComponent.getKoin

val internalBridge: InternalBridge
    get() = getKoin().get<Bridge>() as InternalBridge

abstract class InternalBridge : Bridge {
    override val servers: MutableSet<BridgeServer> = mutableSetOf()
}