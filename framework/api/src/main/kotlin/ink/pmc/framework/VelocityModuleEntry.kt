package ink.pmc.framework

import com.velocitypowered.api.proxy.ProxyServer
import java.io.File
import java.util.logging.Logger

abstract class VelocityModuleEntry(
    override val server: ProxyServer,
    override val dataFolder: File,
    override val logger: Logger
) : ModuleEntry<ProxyServer>