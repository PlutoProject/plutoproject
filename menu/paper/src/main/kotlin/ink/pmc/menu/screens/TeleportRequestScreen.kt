package ink.pmc.menu.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.essentials.COMMAND_TPAHERE_SUCCEED
import ink.pmc.essentials.COMMAND_TPA_SUCCEED
import ink.pmc.essentials.api.teleport.TeleportDirection
import ink.pmc.essentials.api.teleport.TeleportManager
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
import ink.pmc.interactive.api.inventory.stateTransition
import ink.pmc.menu.messages.*
import ink.pmc.framework.utils.chat.DURATION
import ink.pmc.framework.utils.chat.UI_PAGING_SOUND
import ink.pmc.framework.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.data.safeSubList
import ink.pmc.framework.utils.dsl.itemStack
import ink.pmc.framework.utils.platform.paper
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.meta.SkullMeta
import org.koin.compose.koinInject

private const val PLAYERS_PRE_COLUMN = 7
private const val COLUMNS_PRE_PAGE = 3
private const val PLAYERS_PRE_PAGE = PLAYERS_PRE_COLUMN * COLUMNS_PRE_PAGE

class TeleportRequestScreen : Screen {

    override val key: ScreenKey = "menu_teleport_player_picker"

    private val localState: ProvidableCompositionLocal<MutableState<Int>> = staticCompositionLocalOf {
        error("Unreachable")
    }
    private val localPlayers: ProvidableCompositionLocal<List<Player>> = staticCompositionLocalOf {
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
            title = TR_TITLE,
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
        val player = LocalPlayer.current
        val navigator = LocalNavigator.currentOrThrow
        /*
        * 0 -> 正常预览中
        * 1 -> 加载中
        * 2 -> 加载完成但没有其他玩家
        * 3 -> 已发送
        * */
        val state = rememberSaveable { mutableStateOf(1) }
        val players = rememberSaveable { mutableStateOf(mutableListOf<Player>()) }
        val index = rememberSaveable { mutableStateOf(0) }
        val maxIndex by rememberSaveable {
            derivedStateOf {
                players.value.getMaxIndex()
            }
        }

        LaunchedEffect(Unit) {
            val online = paper.onlinePlayers.filter { it != player }
            if (online.isEmpty()) {
                state.value = 2
                return@LaunchedEffect
            }
            players.value.addAll(online)
            state.value = 0
        }

        if (navigator.canPop) {
            Row(modifier = Modifier.fillMaxWidth().height(1)) {
                Back()
            }
        }

        CompositionLocalProvider(
            localState provides state,
            localPlayers provides players.value,
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
                name = TR_LOADING
            )
        }
    }

    @Composable
    @Suppress("FunctionName")
    fun Empty() {
        BaseCenter {
            Item(
                material = Material.MINECART,
                name = TR_EMPTY
            )
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Base(contents: ComposableFunction) {
        Row(modifier = Modifier.fillMaxWidth().height(COLUMNS_PRE_PAGE), horizontalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.fillMaxHeight().width(PLAYERS_PRE_COLUMN)) {
                if (localState.current.value != 3) {
                    VerticalGrid(modifier = Modifier.fillMaxSize()) {
                        repeat(PLAYERS_PRE_PAGE) {
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
    private fun Player(player: Player) {
        val current = LocalPlayer.current
        val navigator = LocalNavigator.currentOrThrow.parent
        val manager = koinInject<TeleportManager>()
        /*
        * 0 -> 待选
        * 1 -> 已发送
        * */
        val state = rememberSaveable { mutableStateOf(0) }
        var globalState by localState.current
        Item(
            itemStack = itemStack(
                if (globalState == 3 && state.value != 1) {
                    Material.GRAY_STAINED_GLASS_PANE
                } else {
                    Material.PLAYER_HEAD
                }
            ) {
                displayName = when (state.value) {
                    0 -> TR_PLAYER.replace("<player>", player.name)
                    1 -> TR_PLAYER_SENT
                    else -> error("Unreachable")
                }
                lore(
                    when (state.value) {
                        0 -> TR_PLAYER_LORE(player)
                        else -> listOf()
                    }
                )
                meta {
                    if (globalState == 0 || state.value == 1) {
                        this as SkullMeta
                        playerProfile = player.playerProfile
                    }
                    setEnchantmentGlintOverride(state.value > 0)
                }
            },
            modifier = Modifier.clickable {
                if (state.value != 0 || globalState != 0) return@clickable
                if (manager.hasUnfinishedRequest(current)) return@clickable

                val direction = when (clickType) {
                    ClickType.LEFT -> TeleportDirection.GO
                    ClickType.RIGHT -> TeleportDirection.COME
                    else -> return@clickable
                }

                val message = when (direction) {
                    TeleportDirection.GO -> COMMAND_TPA_SUCCEED
                    TeleportDirection.COME -> COMMAND_TPAHERE_SUCCEED
                }

                manager.createRequest(current, player, direction)
                state.stateTransition(1, navigator = navigator, pop = true)
                globalState = 3
                current.playSound(UI_SUCCEED_SOUND)
                current.sendMessage(
                    message
                        .replace("<player>", player.name)
                        .replace("<expire>", DURATION(manager.defaultRequestOptions.expireAfter))
                )
            }
        )
    }

    private fun List<Player>.getPlayers(index: Int) = safeSubList(index, index + PLAYERS_PRE_PAGE)

    private fun List<Player>.getMaxIndex(): Int {
        var start = -1
        while (true) {
            val page = getPlayers(start + 1)
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
            val players = localPlayers.current.getPlayers(index)
            Base {
                VerticalGrid(modifier = Modifier.fillMaxSize()) {
                    players.forEach {
                        Player(it)
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
            name = TR_PAGING
                .replace("<curr>", curr + 1)
                .replace("<total>", max + 1),
            lore = TR_PAGING_LORE,
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