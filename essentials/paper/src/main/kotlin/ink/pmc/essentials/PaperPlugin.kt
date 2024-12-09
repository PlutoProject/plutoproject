package ink.pmc.essentials

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.mojang.brigadier.arguments.StringArgumentType
import com.sksamuel.hoplite.PropertySource
import ink.pmc.essentials.afk.AfkManagerImpl
import ink.pmc.essentials.api.afk.AfkManager
import ink.pmc.essentials.api.back.BackManager
import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.back.BackManagerImpl
import ink.pmc.essentials.commands.*
import ink.pmc.essentials.commands.afk.AfkCommand
import ink.pmc.essentials.commands.back.BackCommand
import ink.pmc.essentials.commands.home.*
import ink.pmc.essentials.commands.teleport.TeleportCommons
import ink.pmc.essentials.commands.teleport.TpaCommand
import ink.pmc.essentials.commands.teleport.TpacceptCommand
import ink.pmc.essentials.commands.teleport.TpcancelCommand
import ink.pmc.essentials.commands.teleport.random.RtpCommand
import ink.pmc.essentials.commands.warp.*
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.home.HomeManagerImpl
import ink.pmc.essentials.hooks.EconomyHook
import ink.pmc.essentials.listeners.*
import ink.pmc.essentials.recipes.NOTEBOOK_RECIPE
import ink.pmc.essentials.recipes.registerVanillaExtend
import ink.pmc.essentials.repositories.BackRepository
import ink.pmc.essentials.repositories.HomeRepository
import ink.pmc.essentials.repositories.WarpRepository
import ink.pmc.essentials.teleport.TeleportManagerImpl
import ink.pmc.essentials.teleport.random.RandomTeleportManagerImpl
import ink.pmc.essentials.warp.WarpManagerImpl
import ink.pmc.framework.utils.command.annotationParser
import ink.pmc.framework.utils.command.commandManager
import ink.pmc.framework.utils.command.getKotlinMethodArgumentParser
import ink.pmc.framework.utils.command.suggestion.PaperPrivilegedSuggestion
import ink.pmc.framework.utils.config.preconfiguredConfigLoaderBuilder
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import ink.pmc.framework.utils.storage.saveResourceIfNotExisted
import io.leangen.geantyref.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import org.incendo.cloud.bukkit.parser.OfflinePlayerParser
import org.incendo.cloud.bukkit.parser.WorldParser
import org.incendo.cloud.minecraft.extras.parser.ComponentParser
import org.incendo.cloud.parser.ParserDescriptor
import org.incendo.cloud.parser.standard.StringParser
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module

var disabled = true
var economyHook: EconomyHook? = null
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

        commandManager().apply {
            parserRegistry().apply {
                registerSuggestionProvider(
                    "rtp-world",
                    PaperPrivilegedSuggestion.of(WorldParser(), RANDOM_TELEPORT_SPECIFIC)
                )
                registerSuggestionProvider(
                    "homes-offlineplayer",
                    PaperPrivilegedSuggestion.of(OfflinePlayerParser(), HOMES_OTHER)
                )
                registerSuggestionProvider(
                    "warps",
                    WarpParser(false)
                )
                registerSuggestionProvider(
                    "warps-without-alias",
                    WarpParser(true)
                )
                registerNamedParser(
                    "warp",
                    ParserDescriptor.of(WarpParser(false), Warp::class.java)
                )
                registerNamedParser(
                    "warp-without-alias",
                    ParserDescriptor.of(WarpParser(true), Warp::class.java)
                )
                registerNamedParser(
                    "spawn",
                    ParserDescriptor.of(SpawnParser(), Warp::class.java)
                )
                registerNamedParser(
                    "editwarp-component",
                    ComponentParser.componentParser(MiniMessage.miniMessage(), StringParser.StringMode.QUOTED)
                )
            }
            brigadierManager().apply {
                registerMapping(TypeToken.get(WarpParser::class.java)) {
                    it.cloudSuggestions().to { parser ->
                        if (!parser.withoutAlias) StringArgumentType.greedyString() else StringArgumentType.string()
                    }
                }
                registerMapping(TypeToken.get(SpawnParser::class.java)) {
                    it.cloudSuggestions().to { StringArgumentType.greedyString() }
                }
                registerMapping(TypeToken.get(getKotlinMethodArgumentParser<CommandSender, Home>())) {
                    it.cloudSuggestions().to { StringArgumentType.greedyString() }
                }
            }
        }.annotationParser().apply {
            parse(
                EssentialsCommand,
                AlignCommand,
                GmCommand,
                HatCommand,
            )

            if (config.teleport.enabled) {
                parse(
                    TeleportCommons,
                    TpacceptCommand,
                    TpaCommand,
                    TpcancelCommand
                )
            }

            if (config.randomTeleport.enabled) {
                parse(RtpCommand)
            }

            if (config.home.enabled) {
                parse(
                    HomeCommons,
                    DelHomeCommand,
                    HomeCommand,
                    HomesCommand,
                    SetHomeCommand
                )
            }

            if (config.warp.enabled) {
                parse(
                    WarpCommons,
                    DelWarpCommand,
                    EditWarpCommand,
                    PreferredSpawnCommand,
                    SetWarpCommand,
                    SpawnCommand,
                    WarpCommand,
                    WarpsCommand
                )
            }

            if (config.back.enabled) {
                parse(BackCommand)
            }

            if (config.afk.enabled) {
                parse(AfkCommand)
            }

            if (config.containerProtection.enabled) {
                if (config.containerProtection.itemframe) {
                    parse(ItemFrameCommand)
                }
                if (config.containerProtection.lectern) {
                    parse(LecternCommand)
                }
            }

            if (config.head.enabled) {
                parse(HeadCommand)
            }
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

        if (config.recipe.enabled) {
            server.pluginManager.registerSuspendingEvents(RecipeListener, this)
        }

        if (config.join.enabled) {
            server.pluginManager.registerSuspendingEvents(JoinListener, this)
        }

        if (config.disableJoinQuitMessage.enabled) {
            server.pluginManager.registerSuspendingEvents(DisableJoinQuitMessageListener, this)
        }

        if (config.demoWorld.enabled) {
            server.pluginManager.registerSuspendingEvents(DemoWorldListener, this)
        }
    }

    private fun registerRecipes() {
        if (!config.recipe.enabled) return
        if (config.recipe.menuItem) {
            server.addRecipe(NOTEBOOK_RECIPE)
        }
        if (config.recipe.vanillaExtend) {
            server.registerVanillaExtend()
        }
    }

    private fun initialize() {
        // 初始化 AfkManager，开始后台任务
        if (config.afk.enabled) get<AfkManager>()

        if (server.pluginManager.getPlugin("Vault") != null) {
            economyHook = EconomyHook()
        }
    }

}