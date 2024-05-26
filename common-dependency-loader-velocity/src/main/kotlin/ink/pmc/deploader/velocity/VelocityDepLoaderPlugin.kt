package ink.pmc.deploader.velocity

import com.google.inject.Inject
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.utils.PLUTO_VERSION
import java.util.logging.Logger

@Plugin(
    id = "common-dependency-loader-velocity",
    name = "common-dependency-loader-velocity",
    version = PLUTO_VERSION
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityDepLoaderPlugin {

    @Inject
    fun depLoaderVelocity(server: ProxyServer, logger: Logger) {
        // 什么也不做
    }

}