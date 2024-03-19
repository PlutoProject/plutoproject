package ink.pmc.common.utils.concurrent

import ink.pmc.common.utils.platform.velocityProxyServer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

val velocityDispatcher: CoroutineDispatcher
    get() = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            velocityProxyServer.scheduler.buildTask(velocityProxyServer, Runnable { block.run() })
        }
    }