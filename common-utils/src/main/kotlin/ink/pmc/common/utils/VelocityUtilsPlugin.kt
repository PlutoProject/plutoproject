package ink.pmc.common.utils

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.common.utils.platform.velocityProxyServer
import ink.pmc.common.utils.platform.velocityUtilsPlugin
import java.util.logging.Logger

lateinit var proxyServer: ProxyServer

@Plugin(
    id = "common-utils",
    name = "common-utils",
    version = "1.0.1",
    dependencies = [Dependency(id = "common-dependency-loader-velocity")]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityUtilsPlugin {

    @Inject
    fun velocityUtils(server: ProxyServer, logger: Logger) {
        proxyServer = server
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        velocityUtilsPlugin = proxyServer.pluginManager.getPlugin("common-utils").get()
        velocityProxyServer = proxyServer
    }

}