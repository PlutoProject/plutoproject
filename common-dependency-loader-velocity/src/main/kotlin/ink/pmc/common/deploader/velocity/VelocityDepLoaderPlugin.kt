package ink.pmc.common.deploader.velocity

import com.google.inject.Inject
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import java.util.logging.Logger

@Plugin(
    id = "common-dependency-loader-velocity",
    name = "common-dependency-loader-velocity",
    version = "1.0.2"
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityDepLoaderPlugin {

    @Inject
    fun depLoaderVelocity(server: ProxyServer, logger: Logger) {
        // 什么也不做
    }

}