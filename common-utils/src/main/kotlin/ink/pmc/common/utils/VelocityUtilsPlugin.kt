package ink.pmc.common.utils

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.common.utils.platform.proxy
import ink.pmc.common.utils.platform.proxyThread
import ink.pmc.common.utils.platform.velocityUtilsPlugin
import java.util.logging.Logger

lateinit var proxyServer: ProxyServer

@Plugin(
    id = "common-utils",
    name = "common-utils",
    version = "1.0.2",
    dependencies = [
        Dependency(id = "common-dependency-loader-velocity"),
        Dependency(id = "floodgate")
    ]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityUtilsPlugin {

    @Inject
    fun velocityUtils(server: ProxyServer, logger: Logger) {
        proxyServer = server
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        proxyThread = Thread.currentThread()
        proxy = proxyServer
        velocityUtilsPlugin = proxyServer.pluginManager.getPlugin("common-utils").get()
    }

}