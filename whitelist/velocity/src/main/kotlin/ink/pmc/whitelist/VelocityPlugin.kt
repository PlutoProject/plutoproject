package ink.pmc.whitelist

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.framework.provider.Provider
import ink.pmc.framework.utils.command.annotationParser
import ink.pmc.framework.utils.command.commandManager
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import ink.pmc.framework.utils.platform.proxy
import org.koin.dsl.module
import java.nio.file.Path
import java.util.logging.Logger

lateinit var plugin: PluginContainer

private const val COLLECTION_NAME = "whitelist"
private val whitelistCollection =
    Provider.defaultMongoDatabase.getCollection<WhitelistModel>(COLLECTION_NAME)
private val velocityModule = module {
    single<WhitelistRepository> { WhitelistRepository(whitelistCollection) }
}

@Suppress("UNUSED", "UNUSED_PARAMETER", "UnusedReceiverParameter")
class VelocityPlugin @Inject constructor(private val spc: SuspendingPluginContainer) {

    init {
        spc.initialize(this)
    }

    @Inject
    fun velocityWhitelist(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        startKoinIfNotPresent {
            modules(velocityModule)
        }
    }

    @Subscribe
    fun ProxyInitializeEvent.e() {
        plugin = proxy.pluginManager.getPlugin("plutoproject_whitelist").get()
        spc.commandManager().annotationParser().apply {
            parse(WhitelistCommand)
        }
        proxy.eventManager.registerSuspend(this@VelocityPlugin, WhitelistListener)
    }

}