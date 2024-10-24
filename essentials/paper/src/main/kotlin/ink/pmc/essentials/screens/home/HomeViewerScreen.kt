package ink.pmc.essentials.screens.home

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
import ink.pmc.essentials.screens.home.HomeViewerScreen.State.*
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.inventory.components.Item
import ink.pmc.interactive.api.inventory.components.Placeholder
import ink.pmc.interactive.api.inventory.components.canvases.Chest
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Box
import ink.pmc.framework.interactive.inventory.layout.Column
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.interactive.api.inventory.modifiers.*
import ink.pmc.interactive.api.inventory.modifiers.click.clickable
import ink.pmc.framework.utils.chat.UI_BACK
import ink.pmc.framework.utils.chat.replace
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.event.inventory.ClickType
import org.koin.compose.koinInject
import java.util.*

class HomeViewerScreen(private val viewing: OfflinePlayer) : Screen {

    private val localState: ProvidableCompositionLocal<State> = staticCompositionLocalOf { error("") }
    private val localCurrIndex: ProvidableCompositionLocal<MutableState<Int>> = staticCompositionLocalOf { error("") }
    private val localMaxIndex: ProvidableCompositionLocal<Int> = staticCompositionLocalOf { error("") }
    private val localPages: ProvidableCompositionLocal<ArrayListMultimap<Int, Home>> =
        staticCompositionLocalOf { error("") }

    override val key: ScreenKey = "essentials_home_viewer_${viewing.uniqueId}"

    private enum class State {
        LOADING, VIEWING, VIEWING_EMPTY
    }

    private suspend fun getPages(manager: HomeManager): Multimap<Int, Home> {
        return ArrayListMultimap.create<Int, Home>().apply {
            val homes = manager.list(viewing).toMutableList()
            val orderedHomes = LinkedList<Home>().apply {
                homes.filter { it.isPreferred || it.isStarred }.forEach {
                    if (it.isPreferred) addFirst(it) else add(it)
                    homes.remove(it)
                }
                addAll(homes)
            }

            var currentPage = 0
            var currentPageCount = 0

            orderedHomes.forEach {
                put(currentPage, it)
                currentPageCount++
                if (currentPageCount >= VIEWER_SINGLE_PAGE) {
                    currentPage++
                    currentPageCount = 0
                }
            }
        }
    }

    private fun List<Home>.getRows(): Int {
        return if (isEmpty()) 0 else Math.ceilDiv(size, VIEWER_SINGLE_ROW)
    }

    private fun List<Home>.getRow(int: Int): List<Home> {
        val start = int * VIEWER_SINGLE_ROW
        val end = ((int + 1) * VIEWER_SINGLE_ROW).let { if (it < size) it else size }
        return subList(start, end)
    }

    @Composable
    override fun Content() {
        val player = LocalPlayer.current
        val manager = koinInject<HomeManager>()
        var state by rememberSaveable { mutableStateOf(LOADING) }
        val currIndex = rememberSaveable { mutableStateOf(0) }
        var maxIndex by rememberSaveable { mutableStateOf(0) }

        val title by rememberSaveable {
            derivedStateOf {
                val viewingName = viewing.name ?: viewing.uniqueId
                when (state) {
                    LOADING -> UI_VIEWER_LOADING_TITLE
                    else -> {
                        val title = if (player != viewing) UI_HOME_TITLE else UI_HOME_TITLE_SELF
                        title.replace("<player>", viewingName)
                    }
                }
            }
        }
        val pages by rememberSaveable { mutableStateOf(ArrayListMultimap.create<Int, Home>()) }

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


    inner class ViewingScreen(private val index: Int) : Screen {
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
        Navigator(ViewingScreen(localCurrIndex.current.value))
    }

    @Composable
    @Suppress("FunctionName")
    private fun Homes(index: Int) {
        val navigator = LocalNavigator.currentOrThrow
        val page = localPages.current.get(index)
        val rowCount = page.getRows()

        if (rowCount <= 0) {
            navigator.pop()
            return
        }

        Column(modifier = Modifier.fillMaxWidth().height(3)) {
            repeat(rowCount) {
                Row(modifier = Modifier.fillMaxWidth().height(1)) {
                    val row = page.getRow(it)
                    row.forEach { if (it.isLoaded) Home(it) }
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Home(home: Home) {
        val player = LocalPlayer.current
        val navigator = requireNotNull(LocalNavigator.current?.parent) { "Cannot obtain root navigator" }
        Item(
            material = if (!home.isPreferred) Material.PAPER else Material.SUNFLOWER,
            name = UI_HOME_ITEM_NAME.replace("<name>", home.name),
            lore = UI_HOME_ITEM_LORE(home),
            enchantmentGlint = home.isPreferred || home.isStarred,
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        home.teleport(player)
                        player.closeInventory()
                    }

                    ClickType.RIGHT -> {
                        navigator.push(HomeEditorScreen(home))
                    }

                    else -> {}
                }
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun NavBar() {
        val player = LocalPlayer.current
        val max = localMaxIndex.current
        Box(modifier = Modifier.fillMaxWidth().height(1)) {
            Row(modifier = Modifier.fillMaxSize()) {
                repeat(9) {
                    Placeholder()
                }
            }
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                if (player == viewing) {
                    Create(true)
                }
                if (max > 0) {
                    Placeholder()
                    Paging()
                }
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
                            navigator.push(ViewingScreen(curr + 1))
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
    private fun Create(nested: Boolean = false) {
        // 有在 ViewingScreen 和根菜单上展示两种情况
        val navigator = if (nested) LocalNavigator.currentOrThrow.parent else LocalNavigator.currentOrThrow
        Item(
            material = Material.OAK_SIGN,
            name = UI_HOME_VIEWER_CREATE,
            lore = UI_HOME_VIEWER_CREATE_LORE,
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                navigator?.push(HomeCreatorScreen())
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
            Box(modifier = Modifier.fillMaxWidth().height(1)) {
                Row(modifier = Modifier.fillMaxSize()) {
                    repeat(9) {
                        Placeholder()
                    }
                }
                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                    Create()
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Empty() {
        val player = LocalPlayer.current
        Item(
            material = Material.MINECART,
            name = UI_VIEWER_EMPTY,
            lore = if (player == viewing) UI_HOME_EMPTY_LORE else UI_HOME_EMPTY_LORE_OTHER
        )
    }

}