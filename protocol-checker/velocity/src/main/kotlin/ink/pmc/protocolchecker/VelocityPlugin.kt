package ink.pmc.protocolchecker

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PreLoginEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.network.ProtocolVersion
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerPing
import ink.pmc.utils.platform.proxy
import ink.pmc.utils.platform.saveConfig
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

var disabled = false
lateinit var pluginContainer: PluginContainer
lateinit var serverLogger: Logger
lateinit var dataDir: File
lateinit var config: FileConfig

lateinit var protocolRange: IntRange

val Int.gameVersion: List<String>
    get() {
        return ProtocolVersion.getProtocolVersion(this).versionsSupportedBy
    }

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun velocityProtocolChecker(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        serverLogger = logger
        dataDir = dataDirectoryPath.toFile()

        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }

        val configFile = File(dataDir, "proxy_config.conf")

        if (!configFile.exists()) {
            saveConfig(VelocityPlugin::class.java, "proxy_config.conf", configFile)
        }

        config = configFile.loadConfig()
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        pluginContainer = proxy.pluginManager.getPlugin("protocol-checker").get()
        disabled = false
    }

    @Subscribe
    fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        disabled = true
    }

    @Subscribe(order = PostOrder.FIRST)
    fun ProxyPingEvent.e() {
        val protocol = connection.protocolVersion.protocol

        if (protocolRange.contains(protocol)) {
            return
        }

        val newPing = ping.asBuilder()
            .version(ServerPing.Version(protocolRange.first, protocolRange.first.gameVersion.first()))
            .build()

        ping = newPing
        result = ResultedEvent.GenericResult.denied()
    }

    @Subscribe(order = PostOrder.FIRST)
    fun PreLoginEvent.e() {
        val protocol = connection.protocolVersion.protocol

        if (protocolRange.contains(protocol)) {
            return
        }

        result = PreLoginEvent.PreLoginComponentResult.denied(VERSION_NOT_SUPPORTED)
    }

    private fun File.loadConfig(): FileConfig {
        return FileConfig.builder(this)
            .autoreload()
            .onAutoReload {
                val list = config.get<List<Int>>("protocol-range")
                protocolRange = list[0]..list[1]
                serverLogger.info("Reloaded protocol settings")
            }
            .async()
            .build()
            .apply { load() }
    }

}