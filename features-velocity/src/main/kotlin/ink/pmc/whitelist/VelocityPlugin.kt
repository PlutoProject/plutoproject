package ink.pmc.whitelist

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.provider.ProviderService
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.platform.proxy
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager
import org.koin.dsl.module
import java.nio.file.Path
import java.util.logging.Logger

lateinit var plugin: PluginContainer
lateinit var commandManager: VelocityCommandManager<CommandSource>

private const val COLLECTION_NAME = "whitelist"
private val whitelistCollection =
    ProviderService.defaultMongoDatabase.getCollection<WhitelistModel>(COLLECTION_NAME)
private val velocityModule = module {
    single<WhitelistRepository> { WhitelistRepository(whitelistCollection) }
}

@Suppress("UNUSED", "UNUSED_PARAMETER", "UnusedReceiverParameter")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun velocityWhitelist(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        startKoinIfNotPresent {
            modules(velocityModule)
        }
    }

    @Subscribe
    fun ProxyInitializeEvent.e() {
        plugin = proxy.pluginManager.getPlugin("whitelist").get()
        commandManager = VelocityCommandManager(
            plugin,
            proxy,
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
        ).apply { whitelist() }
        proxy.eventManager.registerSuspend(this@VelocityPlugin, WhitelistListener)
    }

}