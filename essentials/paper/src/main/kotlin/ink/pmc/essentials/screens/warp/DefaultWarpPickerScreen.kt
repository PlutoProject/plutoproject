package ink.pmc.essentials.screens.warp

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.inventory.components.*
import ink.pmc.interactive.api.inventory.components.canvases.Chest
import ink.pmc.interactive.api.inventory.jetpack.Arrangement
import ink.pmc.interactive.api.inventory.layout.Box
import ink.pmc.interactive.api.inventory.layout.Column
import ink.pmc.interactive.api.inventory.layout.Row
import ink.pmc.interactive.api.inventory.modifiers.*
import ink.pmc.interactive.api.inventory.modifiers.click.clickable
import ink.pmc.framework.utils.chat.UI_PAGING_SOUND
import ink.pmc.framework.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.data.safeSubList
import ink.pmc.framework.utils.dsl.itemStack
import ink.pmc.framework.utils.visual.*
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.seconds

private const val WARPS_PRE_COLUMN = 7
private const val COLUMNS_PRE_PAGE = 3
private const val WARPS_PRE_PAGE = WARPS_PRE_COLUMN * COLUMNS_PRE_PAGE

class DefaultWarpPickerScreen : Screen, KoinComponent {

    private val warpManager by inject<WarpManager>()
    private val conf by inject<EssentialsConfig>()

    override val key: ScreenKey = "essentials_default_spawn_picker"

    private val localState: ProvidableCompositionLocal<MutableState<Int>> = staticCompositionLocalOf {
        error("Unreachable")
    }
    private val localSpawns: ProvidableCompositionLocal<List<Warp>> = staticCompositionLocalOf {
        error("Unreachable")
    }
    private val localIndex: ProvidableCompositionLocal<MutableState<Int>> = staticCompositionLocalOf {
        error("Unreachable")
    }
    private val localMaxIndex: ProvidableCompositionLocal<Int> = staticCompositionLocalOf {
        error("Unreachable")
    }

    @Composable
    override fun Content() {
        Chest(
            title = Component.text("选择主城"),
            modifier = Modifier.height(5)
        ) {
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
        val navigator = LocalNavigator.currentOrThrow

        /*
        * 0 -> 正常预览中
        * 1 -> 加载中
        * 2 -> 加载完成但没有传送点
        * 3 -> 已选
        * */
        val state = rememberSaveable { mutableStateOf(1) }
        val spawns = rememberSaveable { mutableStateListOf<Warp>() }
        val index = rememberSaveable { mutableStateOf(0) }
        val maxIndex by rememberSaveable { derivedStateOf { spawns.getMaxIndex() } }

        LaunchedEffect(Unit) {
            val fetched = warpManager.listSpawns()
            if (fetched.isEmpty()) {
                state.value = 2
                return@LaunchedEffect
            }
            spawns.addAll(fetched)
            state.value = 0
        }

        if (navigator.canPop) {
            Row(modifier = Modifier.fillMaxWidth().height(1)) {
                Back()
            }
        }

        CompositionLocalProvider(
            localState provides state,
            localSpawns provides spawns,
            localIndex provides index,
            localMaxIndex provides maxIndex
        ) {
            Picker()
        }
    }

    @Composable
    @Suppress("FunctionName")
    fun Picker() {
        Navigator(PickerSection(localIndex.current.value))
    }

    @Composable
    @Suppress("FunctionName")
    fun Loading() {
        BaseCenter {
            Item(
                material = Material.PAPER,
                name = component {
                    text("正在加载数据...") with mochaSubtext0 without italic()
                }
            )
        }
    }

    @Composable
    @Suppress("FunctionName")
    fun Empty() {
        BaseCenter {
            Item(
                material = Material.MINECART,
                name = component {
                    text("服务器似乎还没有设置主城...") with mochaSubtext0 without italic()
                }
            )
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Base(contents: ComposableFunction) {
        Row(modifier = Modifier.fillMaxWidth().height(COLUMNS_PRE_PAGE), horizontalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.fillMaxHeight().width(WARPS_PRE_COLUMN)) {
                if (localState.current.value != 3) {
                    VerticalGrid(modifier = Modifier.fillMaxSize()) {
                        repeat(WARPS_PRE_PAGE) {
                            Space()
                        }
                    }
                }
                contents()
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun BaseCenter(contents: ComposableFunction) {
        Base {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    contents()
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Spawn(spawn: Warp) {
        val player = LocalPlayer.current
        val navigator = LocalNavigator.currentOrThrow.parent

        /*
        * 0 -> 待选
        * 1 -> 已选
        * */
        var state by rememberSaveable { mutableStateOf(0) }
        var globalState by localState.current

        Item(
            itemStack = itemStack(
                if (globalState == 3 && state != 1) {
                    Material.GRAY_STAINED_GLASS_PANE
                } else {
                    Material.PAPER
                }
            ) {
                displayName {
                    when (state) {
                        0 -> {
                            if (spawn.alias == null) {
                                text(spawn.name) with mochaPink without italic()
                            } else {
                                text(spawn.alias!!) with mochaPink without italic()
                                text(" (${spawn.name})") with mochaSubtext0 without italic()
                            }
                        }

                        1 -> {
                            text("√ 已保存") with mochaGreen without italic()
                        }
                    }
                }
                lore(
                    when (state) {
                        0 -> listOf(
                            component {
                                val world = conf.WorldAliases()[spawn.location.world]
                                val x = spawn.location.blockX
                                val y = spawn.location.blockY
                                val z = spawn.location.blockZ
                                text("$world $x, $y, $z") with mochaSubtext0 without italic()
                            },
                            Component.empty(),
                            component {
                                text("左键 ") with mochaLavender without italic()
                                text("设为首选") with mochaText without italic()
                            }
                        )

                        1 -> listOf()
                        else -> error("Unreachable")
                    }
                )
                meta {
                    setEnchantmentGlintOverride(state == 1)
                }
            },
            modifier = Modifier.clickable {
                if (state != 0 || globalState != 0) return@clickable
                if (clickType != ClickType.LEFT) return@clickable

                submitAsync {
                    warpManager.setPreferredSpawn(player, spawn)
                }

                player.playSound(UI_SUCCEED_SOUND)
                state = 1
                globalState = 3
                delay(1.seconds)
                navigator?.pop()
            }
        )
    }

    private fun List<Warp>.getSpawns(index: Int) = safeSubList(index, index + WARPS_PRE_PAGE)

    private fun List<Warp>.getMaxIndex(): Int {
        var start = -1
        while (true) {
            val page = getSpawns(start + 1)
            if (page.isEmpty()) break
            start++
        }
        return start
    }

    inner class PickerSection(private val index: Int) : Screen {

        override val key: ScreenKey = "menu_teleport_player_picker_section"

        @Composable
        override fun Content() {
            var currIndex by localIndex.current
            val maxIndex = localMaxIndex.current
            val state by localState.current

            /*
            * 若将此处的逻辑放在 InnerContents，
            * 一旦 localState 被修改，就会重置 PickerSection 的 Navigator，
            * 导致一些状态更改无法被应用。
            * 将逻辑放入 PickerSection 本身以解决此问题。
            * */
            when (state) {
                1 -> {
                    Loading()
                    return
                }

                2 -> {
                    Empty()
                    return
                }

                else -> {}
            }

            currIndex = index
            val spawns = localSpawns.current.getSpawns(index)

            Base {
                VerticalGrid(modifier = Modifier.fillMaxSize()) {
                    spawns.forEach {
                        Spawn(it)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
                if (state == 3 || maxIndex <= 0) return@Row
                Paging()
            }
        }

    }

    @Composable
    @Suppress("FunctionName")
    private fun Paging() {
        val navigator = LocalNavigator.currentOrThrow
        val curr = localIndex.current.value
        val max = localMaxIndex.current

        Item(
            material = Material.ARROW,
            name = component {
                text("页 ${curr + 1}/${max + 1}") with mochaText without italic()
            },
            lore = listOf(
                Component.empty(),
                component {
                    text("左键 ") with mochaLavender without italic()
                    text("下一页") with mochaText without italic()
                },
                component {
                    text("右键 ") with mochaLavender without italic()
                    text("上一页") with mochaText without italic()
                },
            ),
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        if (curr < max) {
                            navigator.push(PickerSection(curr + 1))
                            whoClicked.playSound(UI_PAGING_SOUND)
                        }
                    }

                    ClickType.RIGHT -> {
                        if (navigator.canPop) {
                            navigator.pop()
                            whoClicked.playSound(UI_PAGING_SOUND)
                        }
                    }

                    else -> {}
                }
            }
        )
    }

}