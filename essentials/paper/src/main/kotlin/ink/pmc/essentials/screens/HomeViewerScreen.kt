package ink.pmc.essentials.screens

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
import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.screens.HomeViewerScreen.State.*
import ink.pmc.interactive.inventory.components.Item
import ink.pmc.interactive.inventory.components.Placeholder
import ink.pmc.interactive.inventory.components.canvases.Chest
import ink.pmc.interactive.inventory.jetpack.Arrangement
import ink.pmc.interactive.inventory.layout.Box
import ink.pmc.interactive.inventory.layout.Column
import ink.pmc.interactive.inventory.layout.Row
import ink.pmc.interactive.inventory.modifiers.*
import ink.pmc.interactive.inventory.modifiers.click.clickable
import ink.pmc.utils.chat.UI_BACK
import ink.pmc.utils.chat.replace
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.koin.compose.koinInject

private const val HOME_ROWS = 3
private const val SINGLE_ROW_HOMES = 7
private const val SINGLE_PAGE_HOMES = SINGLE_ROW_HOMES * HOME_ROWS

class HomeViewerScreen(
    private val player: Player,
    private val viewing: OfflinePlayer,
) : Screen {

    private val localState: ProvidableCompositionLocal<State> = staticCompositionLocalOf { error("") }
    private val localCurrIndex: ProvidableCompositionLocal<MutableState<Int>> = staticCompositionLocalOf { error("") }
    private val localMaxIndex: ProvidableCompositionLocal<Int> = staticCompositionLocalOf { error("") }
    private val localPages: ProvidableCompositionLocal<ArrayListMultimap<Int, Home>> =
        staticCompositionLocalOf { error("") }

    override val key: ScreenKey = "essentials_home_viewer_${viewing.uniqueId}"

    enum class State {
        LOADING, VIEWING, VIEWING_EMPTY
    }

    private suspend fun getPages(manager: HomeManager): Multimap<Int, Home> {
        return ArrayListMultimap.create<Int, Home>().apply {
            val homes = manager.list(viewing) as List
            var currentPage = 0
            var homesOnCurrentPage = 0
            homes.forEach {
                put(currentPage, it)
                homesOnCurrentPage++
                if (homesOnCurrentPage >= SINGLE_PAGE_HOMES) {
                    currentPage++
                    homesOnCurrentPage = 0
                }
            }
        }
    }

    private fun List<Home>.getRows(): Int {
        return if (isEmpty()) 0 else Math.ceilDiv(size, SINGLE_ROW_HOMES)
    }

    private fun List<Home>.getRow(int: Int): List<Home> {
        val start = int * SINGLE_ROW_HOMES
        val end = ((int + 1) * SINGLE_ROW_HOMES).let { if (it < size) it else size }
        return subList(start, end)
    }

    @Composable
    override fun Content() {
        val manager = koinInject<HomeManager>()
        var state by rememberSaveable { mutableStateOf(LOADING) }
        val currIndex = rememberSaveable { mutableStateOf(0) }
        var maxIndex by rememberSaveable { mutableStateOf(0) }

        val title by rememberSaveable {
            derivedStateOf {
                when (state) {
                    LOADING -> UI_HOME_LOADING_TITLE
                    VIEWING -> {
                        val title = if (currIndex.value != maxIndex && currIndex.value != 0) {
                            if (player != viewing) UI_HOME_TITLE else UI_HOME_SELF_TITILE
                        } else {
                            if (player != viewing) UI_HOME_ONE_PAGE_TITLE else UI_HOME_ONE_PAFG_SELF_TITILE
                        }
                        title
                            .replace("<player>", viewing.name!!)
                            .replace("<curr>", "${currIndex.value + 1}")
                            .replace("<total>", "${maxIndex + 1}")
                    }

                    VIEWING_EMPTY -> UI_HOME_ONE_PAGE_TITLE.replace("<player>", viewing.name!!)
                }
            }
        }
        val pages by rememberSaveable { mutableStateOf(ArrayListMultimap.create<Int, Home>()) }

        LaunchedEffect(Unit) {
            val lookup = getPages(manager)
            if (lookup.size() > 0) {
                pages.putAll(lookup)
                maxIndex = lookup.keySet().size - 1
                state = VIEWING
            } else {
                state = VIEWING_EMPTY
            }
        }

        CompositionLocalProvider(
            localState provides state,
            localCurrIndex provides currIndex,
            localMaxIndex provides maxIndex,
            localPages provides pages,
        ) {
            Chest(
                viewers = setOf(player),
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
        override val key: ScreenKey = "essentials_home_viewer_nested_${viewing.uniqueId}"

        @Composable
        override fun Content() {
            localCurrIndex.current.value = index
            Column(modifier = Modifier.width(7).height(4)) {
                Homes(index)
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
    private fun Homes(index: Int) {
        val page = localPages.current.get(index)
        val rowCount = page.getRows()
        Column(modifier = Modifier.fillMaxWidth().height(3)) {
            repeat(rowCount) {
                Row(modifier = Modifier.fillMaxWidth().height(1)) {
                    val row = page.getRow(it)
                    row.forEach { Home(it) }
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Home(home: Home) {
        Item(
            material = Material.PAPER,
            name = UI_HOME_ITEM_NAME.replace("<name>", home.name),
            lore = UI_HOME_ITEM_LORE(home),
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        home.teleport(player)
                        player.closeInventory()
                    }

                    ClickType.RIGHT -> {}
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
            name = UI_HOME_ITEM_PAGING
                .replace("<curr>", "$currPage")
                .replace("<total>", "$totalPage"),
            lore = UI_HOME_ITEM_PAGING_LORE,
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        if (curr < max) {
                            navigator.push(NestedViewingScreen(curr + 1))
                            whoClicked.playSound(UI_HOME_PAGING_SOUND)
                        }
                    }

                    ClickType.RIGHT -> {
                        if (navigator.canPop) {
                            navigator.pop()
                            whoClicked.playSound(UI_HOME_PAGING_SOUND)
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
            name = UI_HOME_LOADING
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
            name = UI_HOME_EMPTY,
            lore = UI_HOME_EMPTY_LORE
        )
    }

}