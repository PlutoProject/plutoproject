package ink.pmc.utils

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.utils.bedrock.floodgateApi
import ink.pmc.utils.bedrock.floodgateApiClass
import ink.pmc.utils.bedrock.floodgateSupport
import ink.pmc.utils.bedrock.isFloodgatePlayer
import ink.pmc.utils.jvm.byteBuddy
import ink.pmc.utils.platform.proxy
import ink.pmc.utils.platform.proxyThread
import ink.pmc.utils.platform.utilsLogger
import ink.pmc.utils.platform.velocityUtilsPlugin
import java.util.*
import java.util.logging.Logger

lateinit var proxyServer: ProxyServer

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun velocityUtils(server: ProxyServer, logger: Logger) {
        proxyServer = server
        proxy = proxyServer
        utilsLogger = logger
        byteBuddy // 初始化
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        proxyThread = Thread.currentThread()
        velocityUtilsPlugin = proxyServer.pluginManager.getPlugin("utils").get()

        if (floodgateSupport()) {
            floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi")
            floodgateApi = floodgateApiClass.getDeclaredMethod("getInstance").invoke(null)
            isFloodgatePlayer = floodgateApiClass.getDeclaredMethod("isFloodgatePlayer", UUID::class.java)
        }
    }

}