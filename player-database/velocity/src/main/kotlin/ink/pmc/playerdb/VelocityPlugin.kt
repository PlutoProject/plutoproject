package ink.pmc.playerdb

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.utils.inject.startKoinIfNotPresent
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.file.Path
import java.util.logging.Logger

private lateinit var pluginLogger: Logger
private val velocityModule = module {
    single(named("player_database_logger")) { pluginLogger }
    single<Notifier> { ProxyNotifier() }
}

@Suppress("UNUSED", "UNUSED_PARAMETER", "UnusedReceiverParameter")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) : KoinComponent {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun velocityPlayerDb(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        pluginLogger = logger
    }

    @Subscribe
    fun ProxyInitializeEvent.e() {
        startKoinIfNotPresent {
            modules(sharedModule, velocityModule)
        }
        disabled = false
        get<Notifier>() // 初始化 Notifier
    }

    @Subscribe
    fun ProxyShutdownEvent.e() {
        disabled = true
        playerDbScope.cancel()
    }

}