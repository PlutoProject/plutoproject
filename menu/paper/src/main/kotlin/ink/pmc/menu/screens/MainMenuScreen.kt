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
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.screens.home.HomeViewerScreen
import ink.pmc.essentials.screens.warp.DefaultWarpPickerScreen
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.inventory.components.Background
import ink.pmc.interactive.api.inventory.components.Item
import ink.pmc.interactive.api.inventory.components.Space
import ink.pmc.interactive.api.inventory.components.VerticalGrid
import ink.pmc.interactive.api.inventory.components.canvases.Chest
import ink.pmc.interactive.api.inventory.jetpack.Arrangement
import ink.pmc.interactive.api.inventory.layout.Box
import ink.pmc.interactive.api.inventory.layout.Column
import ink.pmc.interactive.api.inventory.layout.Row
import ink.pmc.interactive.api.inventory.modifiers.*
import ink.pmc.interactive.api.inventory.modifiers.click.clickable
import ink.pmc.menu.CO_NEAR_COMMAND
import ink.pmc.menu.economy
import ink.pmc.menu.inspecting
import ink.pmc.menu.messages.*
import ink.pmc.menu.screens.models.MainMenuModel
import ink.pmc.menu.screens.models.MainMenuModel.PreferredHomeState
import ink.pmc.menu.screens.models.MainMenuModel.PreferredSpawnState
import ink.pmc.playerdb.api.PlayerDb
import ink.pmc.utils.chat.MESSAGE_SOUND
import ink.pmc.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.utils.chat.replace
import ink.pmc.utils.visual.mochaSubtext0
import ink.pmc.utils.visual.mochaText
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val PANE_COLUMNS = 4
private const val PANE_COLUMN_WIDTH = 7
private const val PANE_GRIDS = PANE_COLUMNS * PANE_COLUMN_WIDTH
private const val FIRST_OPEN_PROMPT_KEY = "main_menu.showed_first_open_prompt"

class MainMenuScreen : Screen, KoinComponent {

    private val conf by inject<EssentialsConfig>()
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
            Common()
        }
    }

    /*
    * 常用菜单页
    */
    @Composable
    @Suppress("FunctionName")
    private fun Common() {
        Item(
            material = Material.CAMPFIRE,
            name = MAIN_MENU_ITEM_COMMON,
            lore = MAIN_MENU_TAB_LORE
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Pane() {
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
                    Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
                        Home()
                        Spawn()
                        Teleport()
                        RandomTeleport()
                        Lookup()
                    }
                    Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
                        Daily()
                        Coin()
                        Wiki()
                    }
                }
            }
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
                        val world = conf.WorldAliases()[it.location.world]
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

                    ClickType.RIGHT -> navigator.push(DefaultWarpPickerScreen())
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
        val hasUnfinishedTpRequest = Essentials.teleportManager.hasUnfinishedRequest(player)

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

    // 可用，货币不足，该世界不可用
    private enum class RandomTeleportState {
        AVAILABLE, COIN_NOT_ENOUGH, NOT_AVAILABLE
    }

    /*
    * 随机传送
    */
    @Composable
    @Suppress("FunctionName")
    private fun RandomTeleport() {
        val player = LocalPlayer.current
        val world = player.world
        val balance = economy.getBalance(player)
        val requirement = Essentials.randomTeleportManager.getRandomTeleportOptions(world).cost

        val state = when {
            !player.hasPermission(RANDOM_TELEPORT_COST_BYPASS) && balance < requirement -> RandomTeleportState.COIN_NOT_ENOUGH
            !Essentials.randomTeleportManager.isEnabled(world) -> RandomTeleportState.NOT_AVAILABLE
            else -> RandomTeleportState.AVAILABLE
        }

        Item(
            material = Material.AMETHYST_SHARD,
            name = MAIN_MENU_ITEM_HOME_RTP,
            lore = when (state) {
                RandomTeleportState.AVAILABLE -> MAIN_MENU_ITEM_HOME_RTP_LORE
                RandomTeleportState.NOT_AVAILABLE -> MAIN_MENU_ITEM_HOME_RTP_NOT_ENABLED_LORE
                RandomTeleportState.COIN_NOT_ENOUGH -> MAIN_MENU_ITEM_HOME_RTP_COIN_NOT_ENOUGH_LORE
            },
            modifier = Modifier.clickable {
                if (state != RandomTeleportState.AVAILABLE) return@clickable
                if (clickType != ClickType.LEFT) return@clickable
                player.closeInventory()
                Essentials.randomTeleportManager.launch(player, player.world)
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
            material = Material.SPYGLASS,
            name = MAIN_MENU_ITEM_HOME_LOOKUP,
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
    * Wiki
    */
    @Composable
    @Suppress("FunctionName")
    private fun Wiki() {
        val player = LocalPlayer.current
        Item(
            material = Material.BOOK,
            name = MAIN_MENU_ITEM_WIKI,
            lore = MAIN_MENU_ITEM_WIKI_LORE,
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                player.closeInventory()
                player.sendMessage(MAIN_MENU_WIKI)
                player.playSound(MESSAGE_SOUND)
            }
        )
    }

}