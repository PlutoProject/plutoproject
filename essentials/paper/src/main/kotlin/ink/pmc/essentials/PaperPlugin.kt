package ink.pmc.essentials

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.sksamuel.hoplite.PropertySource
import ink.pmc.essentials.afk.AfkManagerImpl
import ink.pmc.essentials.api.afk.AfkManager
import ink.pmc.essentials.api.back.BackManager
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.back.BackManagerImpl
import ink.pmc.essentials.commands.*
import ink.pmc.essentials.commands.warp.*
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.home.HomeManagerImpl
import ink.pmc.essentials.hooks.EconomyHook
import ink.pmc.essentials.hooks.HuskHomesHook
import ink.pmc.essentials.listeners.*
import ink.pmc.essentials.recipes.MENU_ITEM_RECIPE
import ink.pmc.essentials.repositories.BackRepository
import ink.pmc.essentials.repositories.HomeRepository
import ink.pmc.essentials.repositories.WarpRepository
import ink.pmc.essentials.teleport.TeleportManagerImpl
import ink.pmc.essentials.teleport.random.RandomTeleportManagerImpl
import ink.pmc.essentials.warp.WarpManagerImpl
import ink.pmc.framework.utils.command.annotationParser
import ink.pmc.framework.utils.command.commandManager
import ink.pmc.framework.utils.config.preconfiguredConfigLoaderBuilder
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import ink.pmc.framework.utils.storage.saveResourceIfNotExisted
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.bukkit.plugin.Plugin
import org.incendo.cloud.paper.PaperCommandManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module

typealias Cm = PaperCommandManager<CommandSourceStack>

var disabled = true
var economyHook: EconomyHook? = null
var huskHomesHook: HuskHomesHook? = null
lateinit var plugin: Plugin

val essentialsScope = CoroutineScope(Dispatchers.Default)

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {

    private val config by inject<EssentialsConfig>()
    private val bukkitModule = module {
        single<EssentialsConfig> {
            preconfiguredConfigLoaderBuilder()
                .addPropertySource(PropertySource.file(saveResourceIfNotExisted("config.conf")))
                .build()
                .loadConfigOrThrow()
        }
        single<HomeRepository> { HomeRepository() }
        single<WarpRepository> { WarpRepository() }
        single<BackRepository> { BackRepository() }
        single<TeleportManager> {
            check(config.teleport.enabled)
            TeleportManagerImpl()
        }
        single<RandomTeleportManager> {
            check(config.randomTeleport.enabled)
            RandomTeleportManagerImpl()
        }
        single<HomeManager> {
            check(config.home.enabled)
            HomeManagerImpl()
        }
        single<WarpManager> {
            check(config.warp.enabled)
            WarpManagerImpl()
        }
        single<BackManager> {
            check(config.back.enabled)
            BackManagerImpl()
        }
        single<AfkManager> {
            check(config.afk.enabled)
            AfkManagerImpl()
        }
    }

    override suspend fun onEnableAsync() {
        plugin = this

        startKoinIfNotPresent {
            modules(bukkitModule)
        }

        commandManager().annotationParser().apply {
            parse(EssentialsCommand)
            parse(AlignCommand)
            parse(GmCommand)
            parse(HatCommand)
            parse(ItemFrameCommand)
            parse(LecternCommand)
            parse(WarpCommons)
            parse(DelWarpCommand)
            parse(EditWarpCommand)
            parse(PreferredSpawnCommand)
            parse(SetWarpCommand)
            parse(SpawnCommand)
            parse(WarpCommand)
            parse(WarpsCommand)
        }

        registerEvents()
        registerRecipes()
        initialize()
        disabled = false
    }

    override suspend fun onDisableAsync() {
        disabled = true
        essentialsScope.cancel()
        if (config.teleport.enabled) {
            TeleportManager.clearRequest()
        }
    }

    private fun registerEvents() {
        if (config.teleport.enabled) {
            server.pluginManager.registerSuspendingEvents(TeleportListener, this)
        }

        if (config.randomTeleport.enabled) {
            server.pluginManager.registerSuspendingEvents(RandomTeleportListener, this)
        }

        if (config.home.enabled) {
            server.pluginManager.registerSuspendingEvents(HomeListener, this)
        }

        if (config.back.enabled) {
            server.pluginManager.registerSuspendingEvents(BackListener, this)
        }

        if (config.afk.enabled) {
            server.pluginManager.registerSuspendingEvents(AfkListener, this)
        }

        if (config.containerProtection.enabled) {
            if (config.containerProtection.itemframe) {
                server.pluginManager.registerSuspendingEvents(ItemFrameListener, this)
            }
            if (config.containerProtection.lectern) {
                server.pluginManager.registerSuspendingEvents(LecternListener, this)
            }
        }

        if (config.action.enabled) {
            server.pluginManager.registerSuspendingEvents(ActionListener, this)
        }

        if (config.item.enabled) {
            server.pluginManager.registerSuspendingEvents(ItemListener, this)
        }

        if (config.join.enabled) {
            server.pluginManager.registerSuspendingEvents(JoinListener, this)
        }

        if (config.demoWorld.enabled) {
            server.pluginManager.registerSuspendingEvents(DemoWorldListener, this)
        }
    }

    private fun registerRecipes() {
        if (!config.recipe.enabled) return
        if (config.recipe.menuItem) {
            server.addRecipe(MENU_ITEM_RECIPE)
        }
    }

    private fun initialize() {
        // 初始化 AfkManager，开始后台任务
        if (config.afk.enabled) get<AfkManager>()

        if (server.pluginManager.getPlugin("Vault") != null) {
            economyHook = EconomyHook()
        }

        if (server.pluginManager.getPlugin("HuskHomes") != null) {
            huskHomesHook = HuskHomesHook()
        }
    }

}