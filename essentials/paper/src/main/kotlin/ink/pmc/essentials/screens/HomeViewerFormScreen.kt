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
import ink.pmc.interactive.api.LocalGuiScope
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.form.components.simple.FormBack
import ink.pmc.interactive.api.form.components.simple.FormButton
import ink.pmc.interactive.api.form.types.SimpleForm
import ink.pmc.utils.chat.replace
import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer
import org.koin.compose.koinInject
import java.util.*

class HomeViewerFormScreen(private val viewing: OfflinePlayer) : Screen {

    private val localState: ProvidableCompositionLocal<Int> = staticCompositionLocalOf { error("") }
    private val localCurrIndex: ProvidableCompositionLocal<MutableState<Int>> = staticCompositionLocalOf { error("") }
    private val localMaxIndex: ProvidableCompositionLocal<Int> = staticCompositionLocalOf { error("") }
    private val localPages: ProvidableCompositionLocal<ArrayListMultimap<Int, Home>> =
        staticCompositionLocalOf { error("") }

    override val key: ScreenKey = "essentials_home_viewer_form_${viewing.uniqueId}"

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
                if (currentPageCount >= VIEWER_FORM_SINGLE) {
                    currentPage++
                    currentPageCount = 0
                }
            }
        }
    }

    @Composable
    override fun Content() {
        val scope = LocalGuiScope.current
        val player = LocalPlayer.current
        val manager = koinInject<HomeManager>()
        /*
        * 0 -> 加载中
        * 1 -> 展示中
        * 2 -> 展示但为空
        * */
        var state by rememberSaveable { mutableStateOf(0) }
        val curr = rememberSaveable { mutableStateOf(0) }
        var max by rememberSaveable { mutableStateOf(0) }
        val pages by rememberSaveable { mutableStateOf(ArrayListMultimap.create<Int, Home>()) }
        val title by rememberSaveable {
            derivedStateOf {
                val viewingName = viewing.name ?: viewing.uniqueId
                when (state) {
                    0 -> UI_VIEWER_LOADING_TITLE
                    else -> {
                        val title = if (player != viewing) UI_HOME_TITLE else UI_HOME_TITLE_SELF
                        title.replace("<player>", viewingName)
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            when (state) {
                0 -> {
                    val lookup = getPages(manager)
                    if (lookup.isEmpty) {
                        state = 2
                        return@LaunchedEffect
                    }
                    pages.putAll(lookup)
                    max = lookup.keySet().size - 1
                    state = 1
                }

                else -> {}
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                pages.clear()
                state = 0
            }
        }

        CompositionLocalProvider(
            localState provides state,
            localCurrIndex provides curr,
            localMaxIndex provides max,
            localPages provides pages,
        ) {
            SimpleForm(
                title = title,
                content = when (state) {
                    0 -> FORM_VIEWER_LOADING
                    1 -> FORM_HOME_VIEWER_HEADER
                        .replace("<total>", pages.values().size)
                        .replace("<page>", VIEWER_FORM_SINGLE)
                        .replace("<curr>", curr.value + 1)
                        .replace("<max>", max + 1)

                    2 -> FORM_VIEWER_HEADER_EMPTY
                    else -> {
                        Component.empty()
                    }
                }
            ) {
                InnerContents()
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        FormBack()
        if (localState.current == 1) {
            Viewing()
        }
    }


    inner class ViewingScreen(private val index: Int) : Screen {
        override val key: ScreenKey = "essentials_home_viewer_form_nested_${viewing.uniqueId}"

        @Composable
        override fun Content() {
            localCurrIndex.current.value = index
            val navigator = LocalNavigator.currentOrThrow
            val curr = localCurrIndex.current.value
            val max = localMaxIndex.current
            if (curr > 0 && navigator.canPop) Previous()
            Homes(index)
            if (curr < max) Next()
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Viewing() {
        Navigator(ViewingScreen(localCurrIndex.current.value))
    }

    @Composable
    @Suppress("FunctionName")
    private fun Homes(index: Int) {
        val navigator = LocalNavigator.currentOrThrow
        val entries = localPages.current.get(index)

        if (entries.size <= 0) {
            navigator.pop()
            return
        }

        entries.forEach { if (it.isLoaded) Home(it) }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Home(home: Home) {
        val scope = LocalGuiScope.current
        FormButton(
            text = FORM_HOME_ITEM(home),
            onClick = { _, _ ->
                scope.dispose()
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Previous() {
        val navigator = LocalNavigator.currentOrThrow
        FormButton(
            text = FORM_VIEWER_PREVIOUS,
            onClick = { _, _ ->
                if (navigator.canPop) {
                    navigator.pop()
                }
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Next() {
        val navigator = LocalNavigator.currentOrThrow
        val curr = localCurrIndex.current.value
        val max = localMaxIndex.current
        FormButton(
            text = FORM_VIEWER_NEXT,
            onClick = { _, _ ->
                if (curr < max) {
                    navigator.push(ViewingScreen(curr + 1))
                }
            }
        )
    }

}