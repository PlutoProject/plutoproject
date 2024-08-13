package ink.pmc.menu.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import ink.pmc.interactive.api.inventory.components.Background
import ink.pmc.interactive.api.inventory.components.Item
import ink.pmc.interactive.api.inventory.components.VerticalGrid
import ink.pmc.interactive.api.inventory.components.canvases.Chest
import ink.pmc.interactive.api.inventory.jetpack.Arrangement
import ink.pmc.interactive.api.inventory.layout.Box
import ink.pmc.interactive.api.inventory.layout.Column
import ink.pmc.interactive.api.inventory.layout.Row
import ink.pmc.interactive.api.inventory.modifiers.*
import ink.pmc.menu.messages.YUME_MAIN_ITEM_COMMON
import ink.pmc.menu.messages.YUME_MAIN_TAB_LORE
import ink.pmc.menu.messages.YUME_MAIN_TITLE
import org.bukkit.Material

private const val PANE_COLUMES = 4
private const val PANE_COLUME_WIDTH = 7
private const val PANE_GRIDS = PANE_COLUMES * PANE_COLUME_WIDTH

class YumeMainMenuScreen : Screen {

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
        Item(
            material = Material.LANTERN,
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
        Item(
            material = Material.BOOK,
        )
    }

}