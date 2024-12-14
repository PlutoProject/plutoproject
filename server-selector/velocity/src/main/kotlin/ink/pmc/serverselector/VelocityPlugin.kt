package ink.pmc.serverselector

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.framework.options.OptionsManager
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import ink.pmc.serverselector.listeners.AutoJoinListener
import java.nio.file.Path
import java.util.logging.Logger

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(spc: SuspendingPluginContainer) {
    init {
        spc.initialize(this)
    }

    @Inject
    fun serverSelector(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        startKoinIfNotPresent {
            modules(sharedModule)
        }
        OptionsManager.registerOptionDescriptor(AUTO_JOIN_DESCRIPTOR)
        server.eventManager.registerSuspend(this, AutoJoinListener)
    }
}