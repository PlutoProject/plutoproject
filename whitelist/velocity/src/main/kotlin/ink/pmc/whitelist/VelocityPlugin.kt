package ink.pmc.whitelist

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
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

private const val COLLECTION_PREFIX = "whitelist_"
private val whitelistCollection =
    ProviderService.defaultMongoDatabase.getCollection<WhitelistModel>("${COLLECTION_PREFIX}models")
private val velocityModule = module {
    single<WhitelistRepository> { WhitelistRepository(whitelistCollection) }
}

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    @Inject
    fun velocityWhitelist(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        plugin = proxy.pluginManager.getPlugin("whitelist").get()
        commandManager = VelocityCommandManager(
            plugin,
            server,
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
        ).apply { whitelist() }
        proxy.eventManager.registerSuspend(this, WhitelistListener)
        startKoinIfNotPresent {
            modules(velocityModule)
        }
    }

}