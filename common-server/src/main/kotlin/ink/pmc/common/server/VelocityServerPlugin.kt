package ink.pmc.common.server

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import org.incendo.cloud.velocity.VelocityCommandManager
import java.util.logging.Logger

lateinit var proxyServer: ProxyServer
lateinit var proxyLogger: Logger
lateinit var proxyCommandManager: VelocityCommandManager<CommandSource>

@Plugin(
    id = "common-server",
    name = "common-server",
    version = "1.0.1",
    dependencies = [Dependency(id = "common-dependency-loader-velocity"), Dependency(id = "common-utils")]
)
class VelocityServerPlugin {

}