package ink.pmc.utils

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
import ink.pmc.utils.platform.velocityUtilsPlugin
import ink.pmc.utils.scripting.BaseScript
import java.io.File
import java.util.*
import java.util.logging.Logger
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

lateinit var proxyServer: ProxyServer

fun testScript() {
    val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<BaseScript>()
    val script = File("test.kts").toScriptSource()
    val host = BasicJvmScriptingHost()
    host.eval(script, compilationConfiguration, null)
}

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin {

    @Inject
    fun velocityUtils(server: ProxyServer, logger: Logger) {
        testScript()
        proxyServer = server
        byteBuddy // 初始化
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        proxyThread = Thread.currentThread()
        proxy = proxyServer
        velocityUtilsPlugin = proxyServer.pluginManager.getPlugin("utils").get()

        if (floodgateSupport()) {
            floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi")
            floodgateApi = floodgateApiClass.getDeclaredMethod("getInstance").invoke(null)
            isFloodgatePlayer = floodgateApiClass.getDeclaredMethod("isFloodgatePlayer", UUID::class.java)
        }
    }

}