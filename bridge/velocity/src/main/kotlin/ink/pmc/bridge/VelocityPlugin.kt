import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.bridge.proto.GeyserBridgeRpc
import ink.pmc.rpc.api.RpcServer
import ink.pmc.utils.platform.proxy
import java.nio.file.Path
import java.util.logging.Logger

lateinit var pluginContainer: PluginContainer

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun bridgePluginVelocity(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        RpcServer.apply {
            addService(GeyserBridgeRpc())
        }
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        pluginContainer = proxy.pluginManager.getPlugin("bridge").get()
    }

}