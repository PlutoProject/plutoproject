package ink.pmc.options

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.options.listeners.VelocityOptionsListener
import ink.pmc.options.proto.OptionsRpc
import ink.pmc.rpc.api.RpcServer
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.platform.proxy
import org.koin.dsl.module
import java.nio.file.Path
import java.util.logging.Logger

private val velocityModule = module {
    single<OptionsUpdateNotifier> { ProxyOptionsUpdateNotifier() }
}

@Suppress("UNUSED", "UNUSED_PARAMETER", "UnusedReceiverParameter")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {
    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun velocityOptions(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        pluginLogger = logger
        startKoinIfNotPresent {
            modules(commonModule, velocityModule)
        }
        RpcServer.apply {
            addService(OptionsRpc)
        }
    }

    @Subscribe
    fun ProxyInitializeEvent.e() {
        proxy.eventManager.registerSuspend(this@VelocityPlugin, VelocityOptionsListener)
    }

    @Subscribe
    fun ProxyShutdownEvent.e() {
    }
}