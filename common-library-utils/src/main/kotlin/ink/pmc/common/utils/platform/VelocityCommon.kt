package ink.pmc.common.utils.platform

import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer

lateinit var proxyThread: Thread
lateinit var proxy: ProxyServer
lateinit var velocityUtilsPlugin: PluginContainer