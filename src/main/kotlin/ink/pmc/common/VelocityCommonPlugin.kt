package ink.pmc.common

import com.google.inject.Inject
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import java.util.logging.Logger

// 用于解决 runTask 启动服务器时会将项目根项目视为插件进行加载而导致报错的问题

@Deprecated("不再使用 Velocity 测试")
@Plugin(
    id = "common",
    name = "common",
    version = "1.0.2",
    dependencies = [Dependency(id = "common-dependency-loader-velocity")]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityCommonPlugin {

    @Inject
    fun commonVelocity(proxyServer: ProxyServer, proxyLogger: Logger) {
        // 什么也不做
    }

}