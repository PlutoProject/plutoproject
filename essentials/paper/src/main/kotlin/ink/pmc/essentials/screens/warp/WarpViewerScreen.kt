package ink.pmc.essentials.screens.warp

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import ink.pmc.essentials.*
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.screens.warp.WarpViewerScreen.State.*
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.*
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.interactive.inventory.components.canvases.Chest
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Box
import ink.pmc.framework.interactive.inventory.layout.Column
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.utils.chat.UI_BACK
import ink.pmc.framework.utils.chat.replace
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.koin.compose.koinInject

class WarpViewerScreen : Screen {
    private val localState: ProvidableCompositionLocal<State> = staticCompositionLocalOf { error("") }
    private val localCurrIndex: ProvidableCompositionLocal<MutableState<Int>> = staticCompositionLocalOf { error("") }
    private val localMaxIndex: ProvidableCompositionLocal<Int> = staticCompositionLocalOf { error("") }
    private val localPages: ProvidableCompositionLocal<ArrayListMultimap<Int, Warp>> =
        staticCompositionLocalOf { error("") }

    override val key: ScreenKey = "essentials_warp_viewer"

    enum class State {
        LOADING, VIEWING, VIEWING_EMPTY
    }

    private suspend fun getPages(manager: WarpManager): Multimap<Int, Warp> {
        return ArrayListMultimap.create<Int, Warp>().apply {
            val warps = manager.list() as List
            var currentPage = 0
            var currentPageCount = 0
            warps.forEach {
                put(currentPage, it)
                currentPageCount++
                if (currentPageCount >= VIEWER_SINGLE_PAGE) {
                    currentPage++
                    currentPageCount = 0
                }
            }
        }
    }

    private fun List<Warp>.getRows(): Int {
        return if (isEmpty()) 0 else Math.ceilDiv(size, VIEWER_SINGLE_ROW)
    }

    private fun List<Warp>.getRow(int: Int): List<Warp> {
        val start = int * VIEWER_SINGLE_ROW
        val end = ((int + 1) * VIEWER_SINGLE_ROW).let { if (it < size) it else size }
        return subList(start, end)
    }

    @Composable
    override fun Content() {
        val manager = koinInject<WarpManager>()
        var state by rememberSaveable { mutableStateOf(LOADING) }
        val currIndex = rememberSaveable { mutableStateOf(0) }
        var maxIndex by rememberSaveable { mutableStateOf(0) }

        val title by rememberSaveable {
            derivedStateOf {
                when (state) {
                    LOADING -> UI_VIEWER_LOADING_TITLE
                    else -> UI_WARP_TITLE
                }
            }
        }
        val pages by rememberSaveable { mutableStateOf(ArrayListMultimap.create<Int, Warp>()) }

        LaunchedEffect(Unit) {
            when (state) {
                LOADING -> {
                    val lookup = getPages(manager)
                    if (lookup.isEmpty) {
                        state = VIEWING_EMPTY
                        return@LaunchedEffect
                    }
                    pages.putAll(lookup)
                    maxIndex = lookup.keySet().size - 1
                    state = VIEWING
                }

                else -> {}
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                pages.clear()
                state = LOADING
            }
        }

        CompositionLocalProvider(
            localState provides state,
            localCurrIndex provides currIndex,
            localMaxIndex provides maxIndex,
            localPages provides pages,
        ) {
            Chest(
                title = title,
                modifier = Modifier.height(5)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    InnerContents()
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        val state = localState.current
        HorizontalBar()
        Row(modifier = Modifier.fillMaxWidth().height(4)) {
            VerticalBar()
            when (state) {
                LOADING -> LoadingSection()
                VIEWING -> ViewingSection()
                VIEWING_EMPTY -> EmptySection()
            }
            VerticalBar()
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun HorizontalBar() {
        val navigator = LocalNavigator.current
        Box(modifier = Modifier.fillMaxWidth().height(1)) {
            Row(modifier = Modifier.fillMaxSize()) {
                repeat(9) {
                    Placeholder()
                }
            }
            if (navigator?.canPop == false) return@Box
            Navigate()
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Navigate() {
        val navigator = LocalNavigator.currentOrThrow
        Item(
            material = Material.YELLOW_STAINED_GLASS_PANE,
            name = UI_BACK,
            modifier = Modifier.clickable {
                navigator.pop()
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun VerticalBar() {
        Column(modifier = Modifier.fillMaxHeight().width(1)) {
            repeat(4) {
                Placeholder()
            }
        }
    }


    inner class NestedViewingScreen(private val index: Int) : Screen {
        override val key: ScreenKey = "essentials_warp_viewer_nested"

        @Composable
        override fun Content() {
            localCurrIndex.current.value = index
            Column(modifier = Modifier.width(7).height(4)) {
                Warps(index)
                NavBar()
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun ViewingSection() {
        Navigator(NestedViewingScreen(localCurrIndex.current.value))
    }

    @Composable
    @Suppress("FunctionName")
    private fun Warps(index: Int) {
        val page = localPages.current.get(index)
        val rowCount = page.getRows()
        Column(modifier = Modifier.fillMaxWidth().height(3)) {
            repeat(rowCount) {
                Row(modifier = Modifier.fillMaxWidth().height(1)) {
                    val row = page.getRow(it)
                    row.forEach { Warp(it) }
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Warp(warp: Warp) {
        val player = LocalPlayer.current
        val title = if (warp.alias == null) UI_WARP_ITEM_NAME else UI_WARP_ITEM_NAME_ALIAS
        Item(
            material = warp.icon ?: Material.PAPER,
            name = title.replace("<name>", warp.name).replace("<alias>", warp.alias),
            lore = UI_WARP_ITEM_LORE(warp),
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        warp.teleport(player)
                        player.closeInventory()
                    }

                    else -> {}
                }
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun NavBar() {
        val max = localMaxIndex.current
        Box(modifier = Modifier.fillMaxWidth().height(1)) {
            Row(modifier = Modifier.fillMaxSize()) {
                repeat(9) {
                    Placeholder()
                }
            }
            if (max == 0) return@Box
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                Paging()
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Paging() {
        val curr = localCurrIndex.current.value
        val max = localMaxIndex.current
        val currPage = curr + 1
        val totalPage = max + 1
        val navigator = LocalNavigator.currentOrThrow

        Item(
            material = Material.ARROW,
            name = VIEWER_PAGING
                .replace("<curr>", "$currPage")
                .replace("<total>", "$totalPage"),
            lore = VIEWING_PAGE_LORE,
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        if (curr < max) {
                            navigator.push(NestedViewingScreen(curr + 1))
                            whoClicked.playSound(VIEWER_PAGING_SOUND)
                        }
                    }

                    ClickType.RIGHT -> {
                        if (navigator.canPop) {
                            navigator.pop()
                            whoClicked.playSound(VIEWER_PAGING_SOUND)
                        }
                    }

                    else -> {}
                }
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun LoadingSection() {
        Column(modifier = Modifier.width(7).height(4)) {
            Column(modifier = Modifier.fillMaxWidth().height(3), verticalArrangement = Arrangement.Center) {
                Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
                    Loading()
                }
            }
            Row(modifier = Modifier.fillMaxWidth().height(1)) {
                repeat(9) {
                    Placeholder()
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Loading() {
        Item(
            material = Material.PAPER,
            name = UI_VIEWER_LOADING
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun EmptySection() {
        Column(modifier = Modifier.width(7).height(4)) {
            Column(modifier = Modifier.fillMaxWidth().height(3), verticalArrangement = Arrangement.Center) {
                Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
                    Empty()
                }
            }
            Row(modifier = Modifier.fillMaxWidth().height(1)) {
                repeat(9) {
                    Placeholder()
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Empty() {
        Item(
            material = Material.MINECART,
            name = UI_VIEWER_EMPTY,
            lore = UI_WARP_EMPTY_LORE
        )
    }
}