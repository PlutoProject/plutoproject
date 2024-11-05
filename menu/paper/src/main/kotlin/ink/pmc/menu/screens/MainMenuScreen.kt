package ink.pmc.menu.screens

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.daily.screens.DailyCalenderScreen
import ink.pmc.essentials.RANDOM_TELEPORT_COST_BYPASS
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.screens.home.HomeViewerScreen
import ink.pmc.essentials.screens.warp.DefaultSpawnPickerMenu
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.*
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.interactive.inventory.components.canvases.Chest
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Box
import ink.pmc.framework.interactive.inventory.layout.Column
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.playerdb.PlayerDb
import ink.pmc.framework.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.time.formatDuration
import ink.pmc.framework.utils.visual.mochaSubtext0
import ink.pmc.framework.utils.visual.mochaText
import ink.pmc.framework.utils.world.aliasOrName
import ink.pmc.hypervisor.DynamicScheduling
import ink.pmc.hypervisor.DynamicViewDistanceState.*
import ink.pmc.menu.CO_NEAR_COMMAND
import ink.pmc.menu.components.Wiki
import ink.pmc.menu.economy
import ink.pmc.menu.inspecting
import ink.pmc.menu.messages.*
import ink.pmc.menu.screens.MainMenuScreen.RandomTeleportState.*
import ink.pmc.menu.screens.models.MainMenuModel
import ink.pmc.menu.screens.models.MainMenuModel.PreferredHomeState
import ink.pmc.menu.screens.models.MainMenuModel.PreferredSpawnState
import ink.pmc.menu.screens.models.MainMenuModel.Tab.ASSIST
import ink.pmc.menu.screens.models.MainMenuModel.Tab.HOME
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.koin.core.component.KoinComponent
import kotlin.time.Duration.Companion.ZERO

private const val PANE_COLUMNS = 4
private const val PANE_COLUMN_WIDTH = 7
private const val PANE_GRIDS = PANE_COLUMNS * PANE_COLUMN_WIDTH
private const val FIRST_OPEN_PROMPT_KEY = "main_menu.showed_first_open_prompt"

class MainMenuScreen : Screen, KoinComponent {
    private val localScreenModel: ProvidableCompositionLocal<MainMenuModel> =
        staticCompositionLocalOf { error("Unexpected") }

    override val key: ScreenKey = "main_menu"

    @Composable
    override fun Content() {
        val player = LocalPlayer.current
        val screenModel = rememberScreenModel { MainMenuModel(player) }

        // 初次打开或翻到其他页面再回来时触发，重载可能被修改的数据
        LaunchedEffect(Unit) {
            screenModel.refreshPreferredHome()
            screenModel.refreshPreferredSpawn()
            screenModel.refreshCheckInState()
        }

        LaunchedEffect(Unit) {
            val db = PlayerDb.getOrCreate(player.uniqueId)
            if (db.getBoolean(FIRST_OPEN_PROMPT_KEY)) return@LaunchedEffect
            player.sendMessage(MAIN_MENU_FIRST_OPEN_PROMPT)
            db[FIRST_OPEN_PROMPT_KEY] = true
            db.update()
        }

        CompositionLocalProvider(localScreenModel provides screenModel) {
            Chest(title = MAIN_MENU_TITLE, modifier = Modifier.height(6)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Background()
                    Column(modifier = Modifier.fillMaxSize()) {
                        InnerContents()
                    }
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        TopBar()
        Pane()
    }

    @Composable
    @Suppress("FunctionName")
    private fun TopBar() {
        Row(modifier = Modifier.height(1).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TabSwitcher(
                icon = Material.CAMPFIRE,
                name = MAIN_MENU_TAB_HOME,
                lore = listOf(),
                tab = HOME
            )
            Spacer(modifier = Modifier.width(1).height(1))
            TabSwitcher(
                icon = Material.TRIPWIRE_HOOK,
                name = MAIN_MENU_TAB_ASSIST,
                lore = listOf(),
                tab = ASSIST
            )
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun TabSwitcher(
        icon: Material,
        name: Component,
        lore: List<Component>,
        tab: MainMenuModel.Tab
    ) {
        val model = localScreenModel.current
        Item(
            material = icon,
            name = name,
            lore = lore,
            enchantmentGlint = model.tab == tab,
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                if (model.tab == tab) return@clickable
                model.tab = tab
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Pane() {
        val model = localScreenModel.current
        Row(
            modifier = Modifier.height(PANE_COLUMNS).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxHeight().width(PANE_COLUMN_WIDTH)) {
                VerticalGrid(modifier = Modifier.fillMaxSize()) {
                    repeat(PANE_GRIDS) {
                        Space()
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    when (model.tab) {
                        HOME -> HomeTab()
                        ASSIST -> AssistTab()
                    }
                }
            }
        }
    }

    // 主页面
    @Composable
    @Suppress("FunctionName")
    private fun HomeTab() {
        Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
            Home()
            Spawn()
            Teleport()
            RandomTeleport()
            Daily()
        }
        Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
            Coin()
            Space()
            Wiki()
        }
    }

    // 辅助功能页面
    @Composable
    @Suppress("FunctionName")
    private fun AssistTab() {
        Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
            Lookup()
            ViewBoost()
            repeat(3) {
                Space()
            }
        }
        Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {

        }
    }

    /*
    * 家
    */
    @Composable
    @Suppress("FunctionName")
    private fun Home() {
        val player = LocalPlayer.current
        val model = localScreenModel.current
        val navigator = LocalNavigator.currentOrThrow

        Item(
            material = Material.LANTERN,
            name = MAIN_MENU_ITEM_HOME,
            lore = when (model.preferredHomeState) {
                is PreferredHomeState.Loading -> LOADING_LORE
                is PreferredHomeState.Ready -> MAIN_MENU_ITEM_HOME_LORE
                is PreferredHomeState.None -> MAIN_MENU_ITEM_HOME_LORE_NO_PREFER
            },
            // enchantmentGlint = state.value > 0,
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        val preferred = (model.preferredHomeState as? PreferredHomeState.Ready)?.home
                        preferred?.teleport(player)
                    }

                    ClickType.RIGHT -> {
                        navigator.push(HomeViewerScreen(player))
                    }

                    else -> {}
                }
            }
        )
    }

    /*
    * 家
    */
    @Composable
    @Suppress("FunctionName")
    private fun Spawn() {
        val navigator = LocalNavigator.currentOrThrow
        val player = LocalPlayer.current
        val model = localScreenModel.current

        Item(
            material = Material.COMPASS,
            name = MAIN_MENU_ITEM_SPAWN,
            lore = when (model.preferredSpawnState) {
                is PreferredSpawnState.Loading -> LOADING_LORE
                is PreferredSpawnState.Ready -> {
                    val spawn = (model.preferredSpawnState as PreferredSpawnState.Ready).spawn
                    val lore = MAIN_MENU_ITEM_SPAWN_LORE
                    val name = when (spawn.alias) {
                        null -> component { text(spawn.name) with mochaText without italic() }
                        else -> component { text(spawn.alias!!) with mochaText without italic() }
                    }
                    val loc = spawn.let {
                        val world = it.location.world.aliasOrName
                        val x = it.location.blockX
                        val y = it.location.blockY
                        val z = it.location.blockZ
                        component { text("$world $x, $y, $z") with mochaSubtext0 without italic() }
                    }
                    lore.replace("<spawn>", name).replace("<loc>", loc)
                }

                is PreferredSpawnState.None -> MAIN_MENU_ITEM_SPAWN_LORE_NO_PREFERRED
            }.toList(),
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        val spawn = (model.preferredSpawnState as? PreferredSpawnState.Ready)?.spawn ?: return@clickable
                        spawn.teleport(player)
                    }

                    ClickType.RIGHT -> navigator.push(DefaultSpawnPickerMenu())
                    else -> {}
                }
            }
        )
    }

    /*
    * 玩家间传送
    */
    @Composable
    @Suppress("FunctionName")
    private fun Teleport() {
        val player = LocalPlayer.current
        val navigator = LocalNavigator.currentOrThrow
        val hasUnfinishedTpRequest = TeleportManager.hasUnfinishedRequest(player)

        Item(
            material = Material.MINECART,
            name = MAIN_MENU_ITEM_TP,
            lore = if (!hasUnfinishedTpRequest) MAIN_MENU_ITEM_TP_LORE else MAIN_MENU_ITEM_TP_EXISTED_LORE,
            enchantmentGlint = hasUnfinishedTpRequest,
            modifier = Modifier.clickable {
                if (hasUnfinishedTpRequest) return@clickable
                if (clickType != ClickType.LEFT) return@clickable
                navigator.push(TeleportRequestScreen())
            }
        )
    }

    // 可用，货币不足，该世界不可用，冷却中
    private enum class RandomTeleportState {
        AVAILABLE, COIN_NOT_ENOUGH, NOT_AVAILABLE, IN_COOLDOWN
    }

    /*
    * 随机传送
    */
    @Composable
    @Suppress("FunctionName")
    private fun RandomTeleport() {
        val model = localScreenModel.current
        val player = LocalPlayer.current
        val world = player.world
        val balance = economy.getBalance(player)
        val requirement = RandomTeleportManager.getRandomTeleportOptions(world).cost

        LaunchedEffect(Unit) {
            while (true) {
                delay(500)
                model.refreshCooldownState()
            }
        }

        val state = when {
            model.rtpCooldownRemaining > ZERO -> IN_COOLDOWN
            !player.hasPermission(RANDOM_TELEPORT_COST_BYPASS) && balance < requirement -> COIN_NOT_ENOUGH
            !RandomTeleportManager.isEnabled(world) -> NOT_AVAILABLE
            else -> AVAILABLE
        }

        Item(
            material = Material.AMETHYST_SHARD,
            name = MAIN_MENU_ITEM_HOME_RTP,
            lore = when (state) {
                AVAILABLE -> MAIN_MENU_ITEM_HOME_RTP_LORE
                NOT_AVAILABLE -> MAIN_MENU_ITEM_HOME_RTP_NOT_ENABLED_LORE
                COIN_NOT_ENOUGH -> MAIN_MENU_ITEM_HOME_RTP_COIN_NOT_ENOUGH_LORE
                IN_COOLDOWN -> MAIN_MENU_ITEM_LORE_HOME_RTP_IN_COOLDOWN_LORE.replace(
                    "<time>",
                    model.rtpCooldownRemaining.formatDuration()
                ).toList()
            },
            modifier = Modifier.clickable {
                if (state != AVAILABLE) return@clickable
                if (clickType != ClickType.LEFT) return@clickable
                player.closeInventory()
                RandomTeleportManager.launch(player, player.world)
            }
        )
    }

    /*
    * 查询周围
    */
    @Composable
    @Suppress("FunctionName")
    private fun Lookup() {
        val player = LocalPlayer.current
        val model = localScreenModel.current
        Item(
            material = Material.ENDER_EYE,
            name = if (!model.lookupModeEnabled) MAIN_MENU_ITEM_HOME_LOOKUP_OFF else MAIN_MENU_ITEM_HOME_LOOKUP_ON,
            lore = if (!model.lookupModeEnabled) MAIN_MENU_ITEM_HOME_LOOKUP_LORE else MAIN_MENU_ITEM_HOME_LOOKUP_ENABLED_LORE,
            enchantmentGlint = model.lookupModeEnabled,
            modifier = Modifier.clickable {
                if (!model.lookupModeEnabled) {
                    when (clickType) {
                        ClickType.LEFT -> {
                            player.inspecting = true
                            model.lookupModeEnabled = true
                            player.playSound(UI_SUCCEED_SOUND)
                            return@clickable
                        }

                        ClickType.RIGHT -> {
                            player.performCommand(CO_NEAR_COMMAND)
                            player.closeInventory()
                            player.playSound(UI_SUCCEED_SOUND)
                        }

                        else -> {}
                    }
                    return@clickable
                }

                if (model.lookupModeEnabled && clickType == ClickType.LEFT && player.inspecting) {
                    player.inspecting = false
                    model.lookupModeEnabled = false
                    player.playSound(UI_SUCCEED_SOUND)
                    return@clickable
                }
            }
        )
    }

    /*
    * 每日签到
    */
    @Composable
    @Suppress("FunctionName")
    private fun Daily() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = localScreenModel.current
        Item(
            material = Material.NAME_TAG,
            name = MAIN_MENU_ITEMS_DAILY,
            lore = if (!screenModel.isCheckedInToday) MAIN_MENU_ITEMS_DAILY_LORE else MAIN_MENU_ITEMS_DAILY_LORE_CHECKED_IN,
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                navigator.push(DailyCalenderScreen())
            }
        )
    }

    /*
    * 货币信息
    */
    @Composable
    @Suppress("FunctionName")
    private fun Coin() {
        val player = LocalPlayer.current
        Item(
            material = Material.SUNFLOWER,
            name = MAIN_MENU_ITEM_COINS,
            lore = MAIN_MENU_ITEM_COINS_LORE(player),
        )
    }

    /*
    * 视距拓展
    * */
    @Composable
    @Suppress("FunctionName")
    private fun ViewBoost() {
        val player = LocalPlayer.current
        var state by mutableStateOf(DynamicScheduling.getViewDistanceLocally(player))
        Item(
            material = Material.SPYGLASS,
            name = when (state) {
                ENABLED -> MAIN_MENU_ITEM_VIEW_BOOST
                    .append(Component.text(" "))
                    .append(ITEM_ENABLED)

                DISABLED -> MAIN_MENU_ITEM_VIEW_BOOST
                    .append(Component.text(" "))
                    .append(ITEM_DISABLED)

                DISABLED_DUE_PING -> MAIN_MENU_ITEM_VIEW_BOOST
                    .append(Component.text(" "))
                    .append(ITEM_DISABLED)

                ENABLED_BUT_DISABLED_DUE_PING -> MAIN_MENU_ITEM_VIEW_BOOST
                    .append(Component.text(" "))
                    .append(ITEM_DISABLED)

                DISABLED_DUE_VHOST -> MAIN_MENU_ITEM_VIEW_BOOST
                    .append(Component.text(" "))
                    .append(ITEM_DISABLED)
            },
            enchantmentGlint = state == ENABLED,
            lore = when (state) {
                ENABLED -> MAIN_MENU_ITEM_VIEW_BOOST_LORE_ENABLED
                DISABLED -> MAIN_MENU_ITEM_VIEW_BOOST_LORE_DISABLED
                DISABLED_DUE_PING -> MAIN_MENU_ITEM_VIEW_BOOST_LORE_DISABLED_DUE_PING
                ENABLED_BUT_DISABLED_DUE_PING -> MAIN_MENU_ITEM_VIEW_BOOST_LORE_DISABLED_DUE_PING
                DISABLED_DUE_VHOST -> MAIN_MENU_ITEM_VIEW_BOOST_LORE_DISABLED_DUE_VHOST
            },
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                when (state) {
                    ENABLED -> {
                        DynamicScheduling.setViewDistance(player, false)
                        player.playSound(UI_SUCCEED_SOUND)
                        state = DISABLED
                    }

                    DISABLED -> {
                        DynamicScheduling.setViewDistance(player, true)
                        player.playSound(UI_SUCCEED_SOUND)
                        state = ENABLED
                    }

                    DISABLED_DUE_PING -> return@clickable
                    DISABLED_DUE_VHOST -> return@clickable
                    ENABLED_BUT_DISABLED_DUE_PING -> return@clickable
                }
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Recipes() {

    }
}