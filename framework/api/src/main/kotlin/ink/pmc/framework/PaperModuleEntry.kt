package ink.pmc.framework

import org.bukkit.Server
import java.io.File
import java.util.logging.Logger

abstract class PaperModuleEntry(
    override val server: Server,
    override val dataFolder: File,
    override val logger: Logger
) : ModuleEntry<Server>