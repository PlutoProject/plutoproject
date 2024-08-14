package ink.pmc.menu.screens

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.screens.HomeViewerScreen
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.inventory.components.Background
import ink.pmc.interactive.api.inventory.components.Item
import ink.pmc.interactive.api.inventory.components.VerticalGrid
import ink.pmc.interactive.api.inventory.components.canvases.Chest
import ink.pmc.interactive.api.inventory.jetpack.Arrangement
import ink.pmc.interactive.api.inventory.layout.Box
import ink.pmc.interactive.api.inventory.layout.Column
import ink.pmc.interactive.api.inventory.layout.Row
import ink.pmc.interactive.api.inventory.modifiers.*
import ink.pmc.interactive.api.inventory.modifiers.click.clickable
import ink.pmc.menu.messages.*
import ink.pmc.utils.chat.UI_INVALID_SOUND
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.seconds

private const val PANE_COLUMES = 4
private const val PANE_COLUME_WIDTH = 7
private const val PANE_GRIDS = PANE_COLUMES * PANE_COLUME_WIDTH

class YumeMainMenuScreen : Screen {

    override val key: ScreenKey = "menu_yume_main"

    @Composable
    override fun Content() {
        Chest(title = YUME_MAIN_TITLE, modifier = Modifier.height(6)) {
            Box(modifier = Modifier.fillMaxSize()) {
                Background()
                Column(modifier = Modifier.fillMaxSize()) {
                    InnerContents()
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
            name = YUME_MAIN_ITEM_COMMON,
            lore = YUME_MAIN_TAB_LORE
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Pane() {
        Row(
            modifier = Modifier.height(PANE_COLUMES).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxHeight().width(PANE_COLUME_WIDTH)) {
                VerticalGrid(modifier = Modifier.fillMaxSize()) {
                    repeat(PANE_GRIDS) {
                        Item(material = Material.AIR)
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
        val navigator = LocalNavigator.currentOrThrow
        /*
        * 0 -> 正常状态
        * 1 -> 无首选家
        * */
        var state by remember { mutableStateOf(0) }
        val manager = koinInject<HomeManager>()
        val coroutineScope = rememberCoroutineScope()

        fun stateTransition(new: Int) {
            coroutineScope.launch {
                val keep = state
                state = new
                delay(1.seconds)
                state = keep
            }
        }

        Item(
            material = Material.LANTERN,
            name = YUME_MAIN_ITEM_HOME,
            lore = when (state) {
                0 -> YUME_MAIN_ITEM_HOME_LORE
                1 -> YUME_MAIN_ITEM_HOME_LORE_NO_PREFER
                else -> error("Unsupported state")
            },
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        manager.getPreferredHome(player)?.let {
                            it.teleport(player)
                            player.closeInventory()
                            return@clickable
                        }
                        stateTransition(1)
                        player.playSound(UI_INVALID_SOUND)
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
        Item(
            material = Material.COMPASS,
        )
    }

    /*
    * 玩家间传送
    */
    @Composable
    @Suppress("FunctionName")
    private fun Teleport() {
        Item(
            material = Material.MINECART,
        )
    }

    /*
    * 随机传送
    */
    @Composable
    @Suppress("FunctionName")
    private fun RandomTeleport() {
        Item(
            material = Material.AMETHYST_SHARD,
            name = YUME_MAIN_ITEM_HOME_RTP,
            lore = YUME_MAIN_ITEM_HOME_RTP_LORE
        )
    }

    /*
    * 查询周围
    */
    @Composable
    @Suppress("FunctionName")
    private fun Lookup() {
        Item(
            material = Material.SPYGLASS,
        )
    }

    /*
    * 每日签到
    */
    @Composable
    @Suppress("FunctionName")
    private fun Daily() {
        Item(
            material = Material.NAME_TAG,
        )
    }

    /*
    * 货币信息
    */
    @Composable
    @Suppress("FunctionName")
    private fun Coin() {
        Item(
            material = Material.SUNFLOWER,
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
            name = YUME_MAIN_ITEM_WIKI,
            lore = YUME_MAIN_ITEM_WIKI_LORE,
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                player.closeInventory()
                player.sendMessage(YUME_MAIN_WIKI)
            }
        )
    }

}