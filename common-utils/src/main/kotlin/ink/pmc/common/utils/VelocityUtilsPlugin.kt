package ink.pmc.common.utils

import com.google.inject.Inject
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import java.util.logging.Logger

@Plugin(
    id = "common-utils",
    name = "common-utils",
    version = "1.0.0",
    dependencies = [Dependency(id = "common-dependency-loader-velocity")]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityUtilsPlugin {

    @Inject
    fun velocityUtils(server: ProxyServer, logger: Logger) {
        // 什么也不做
    }

}