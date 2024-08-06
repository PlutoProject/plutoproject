package ink.pmc.essentials

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.api.afk.AfkManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.listeners.*
import ink.pmc.utils.command.registerCommands
import ink.pmc.utils.storage.saveResourceIfNotExisted
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.Plugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import java.io.File

typealias Cm = PaperCommandManager<CommandSourceStack>

var disabled = true
var economy: Economy? = null
lateinit var plugin: Plugin
lateinit var fileConfig: FileConfig
lateinit var commandManager: Cm

private const val COMMAND_PACKAGE = "ink.pmc.essentials.commands"
val essentialsScope = CoroutineScope(Dispatchers.Default)

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {

    private val conf by inject<EssentialsConfig>()

    override suspend fun onEnableAsync() {
        plugin = this

        startKoin {
            modules(appModule)
        }

        val config = saveResourceIfNotExisted("config.conf")
        fileConfig = config.loadConfig()

        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)

        commandManager.registerCommands(COMMAND_PACKAGE) {
            conf.Commands()[it] to conf.CommandAliases()[it]
        }

        registerEvents()
        initialize()
        economy = server.servicesManager.getRegistration(Economy::class.java)?.provider
        if (economy == null) logger.info("Cannot obtain Economy API, certain features will be disabled")
        disabled = false
    }

    override suspend fun onDisableAsync() {
        disabled = true
        essentialsScope.cancel()
        Essentials.teleportManager.clearRequest()
    }

    private fun registerEvents() {
        if (Essentials.isTeleportEnabled()) {
            server.pluginManager.registerSuspendingEvents(TeleportListener, this)
        }

        if (Essentials.isRandomTeleportEnabled()) {
            server.pluginManager.registerSuspendingEvents(RandomTeleportListener, this)
        }

        if (Essentials.isHomeEnabled()) {
            server.pluginManager.registerSuspendingEvents(HomeListener, this)
        }

        if (Essentials.isWarpEnabled()) {
            server.pluginManager.registerSuspendingEvents(WarpListener, this)
        }

        if (Essentials.isBackEnabled()) {
            server.pluginManager.registerSuspendingEvents(BackListener, this)
        }

        if (Essentials.isAfkEnabled()) {
            server.pluginManager.registerSuspendingEvents(AfkListener, this)
        }

        if (Essentials.isItemFrameEnabled()) {
            server.pluginManager.registerSuspendingEvents(ItemFrameListener, this)
        }

        if (Essentials.isLecternEnabled()) {
            server.pluginManager.registerSuspendingEvents(LecternListener, this)
        }
    }

    private fun initialize() {
        // 初始化 AfkManager，开始后台任务
        if (Essentials.isAfkEnabled()) {
            get<AfkManager>()
        }
    }

    private fun File.loadConfig(): FileConfig {
        return FileConfig.builder(this)
            .async()
            .autoreload()
            .build()
            .apply { load() }
    }

}