package ink.pmc.common.member

import com.google.inject.Inject
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import java.util.logging.Logger

lateinit var proxyServer: ProxyServer
lateinit var proxyLogger: Logger

@Plugin(
    id = "common-member",
    name = "common-member",
    version = "1.0.0",
    dependencies = [Dependency(id = "common-dependency-loader-velocity")]
)
class MemberPluginVelocity {

    @Inject
    fun memberPluginVelocity(server: ProxyServer, logger: Logger) {
        proxyServer = server
        proxyLogger = logger
    }

}