package ink.pmc.essentials

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.api.afk.AfkManager
import ink.pmc.essentials.commands.warp.defaultSpawnCommand
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.hooks.EconomyHook
import ink.pmc.essentials.hooks.HuskHomesHook
import ink.pmc.essentials.listeners.*
import ink.pmc.essentials.recipes.MENU_ITEM_RECIPE
import ink.pmc.utils.command.CommandRegistrationResult
import ink.pmc.utils.command.registerCommands
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.storage.saveResourceIfNotExisted
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.bukkit.plugin.Plugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.io.File

typealias Cm = PaperCommandManager<CommandSourceStack>

var disabled = true
var economyHook: EconomyHook? = null
var huskHomesHook: HuskHomesHook? = null
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

        startKoinIfNotPresent {
            modules(appModule)
        }

        val config = saveResourceIfNotExisted("config.conf")
        fileConfig = config.loadConfig()

        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)

        commandManager.registerCommands(COMMAND_PACKAGE) {
            CommandRegistrationResult(
                enabled = conf.Commands()[it],
                aliases = conf.CommandAliases()[it]
            )
        }

        commandManager.apply {
            command(defaultSpawnCommand)
        }

        registerEvents()
        registerRecipes()
        initialize()
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

        /*
        if (Essentials.isWarpEnabled()) {
            server.pluginManager.registerSuspendingEvents(WarpListener, this)
        }
        */

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

        if (Essentials.isActionEnabled()) {
            server.pluginManager.registerSuspendingEvents(ActionListener, this)
        }

        if (Essentials.isItemEnabled()) {
            server.pluginManager.registerSuspendingEvents(ItemListener, this)
        }

        if (Essentials.isJoinEnabled()) {
            server.pluginManager.registerSuspendingEvents(JoinListener, this)
        }
    }

    private fun registerRecipes() {
        if (!conf.Recipe().enabled) return
        if (conf.Recipe().menuItem) {
            server.addRecipe(MENU_ITEM_RECIPE)
        }
    }

    private fun initialize() {
        // 初始化 AfkManager，开始后台任务
        if (Essentials.isAfkEnabled()) get<AfkManager>()

        if (server.pluginManager.getPlugin("Vault") != null) {
            economyHook = EconomyHook()
        }

        if (server.pluginManager.getPlugin("HuskHomes") != null) {
            huskHomesHook = HuskHomesHook()
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