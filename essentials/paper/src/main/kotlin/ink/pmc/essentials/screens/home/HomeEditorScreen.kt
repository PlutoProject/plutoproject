package ink.pmc.essentials.screens.home

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.essentials.*
import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.screens.home.HomeEditorScreen.PreferState.NOT_PREFERRED
import ink.pmc.essentials.screens.home.HomeEditorScreen.PreferState.PRRFERRED
import ink.pmc.essentials.screens.home.HomeEditorScreen.StarState.NOT_STARRED
import ink.pmc.essentials.screens.home.HomeEditorScreen.StarState.STARRED
import ink.pmc.essentials.screens.home.HomeEditorScreen.State.*
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.inventory.components.Back
import ink.pmc.interactive.api.inventory.components.Item
import ink.pmc.interactive.api.inventory.components.Placeholder
import ink.pmc.interactive.api.inventory.components.canvases.Anvil
import ink.pmc.interactive.api.inventory.components.canvases.Chest
import ink.pmc.interactive.api.inventory.layout.Box
import ink.pmc.interactive.api.inventory.layout.Column
import ink.pmc.interactive.api.inventory.layout.Row
import ink.pmc.interactive.api.inventory.modifiers.*
import ink.pmc.interactive.api.inventory.modifiers.click.clickable
import ink.pmc.framework.utils.chat.isValidIdentifier
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.dsl.itemStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.wesjd.anvilgui.AnvilGUI.Slot.INPUT_LEFT
import net.wesjd.anvilgui.AnvilGUI.Slot.OUTPUT
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.seconds

class HomeEditorScreen(private val home: Home) : Screen {

    override val key: ScreenKey = "essentials_home_editor_${home.id}"

    @Composable
    override fun Content() {
        Chest(
            title = UI_HOME_EDITOR_TITLE.replace("<name>", home.name),
            modifier = Modifier.height(3)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                InnerContents()
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        HorizontalBar()
        Row(modifier = Modifier.fillMaxWidth().height(1)) {
            VerticalBar()
            Editor()
            VerticalBar()
        }
        HorizontalBar(false)
    }

    @Composable
    @Suppress("FunctionName")
    private fun HorizontalBar(nav: Boolean = true) {
        val navigator = LocalNavigator.currentOrThrow
        Box(modifier = Modifier.fillMaxWidth().height(1)) {
            Row(modifier = Modifier.fillMaxSize()) {
                repeat(9) {
                    Placeholder()
                }
            }
            if (!nav || !navigator.canPop) return@Box
            Back()
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun VerticalBar() {
        Column(modifier = Modifier.fillMaxHeight().width(1)) {
            Placeholder()
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Editor() {
        Row(modifier = Modifier.fillMaxHeight().width(7)) {
            Prefer()
            Star()
            Rename()
            ChangeLocation()
            repeat(2) {
                Placeholder()
            }
            Delete()
        }
    }

    private enum class State {
        EDITING, INVALID, TOO_LONG, EXISTED, SUCCEED
    }

    inner class RenameScreen : Screen {
        override val key: ScreenKey = "essentials_home_editor_rename_${home.id}"

        @Composable
        override fun Content() {
            val player = LocalPlayer.current
            val coroutineScope = rememberCoroutineScope()
            var state by remember { mutableStateOf(EDITING) }
            val navigator = LocalNavigator.currentOrThrow
            val manager = koinInject<HomeManager>()

            fun stateTransition(newState: State, pop: Boolean = false) {
                coroutineScope.launch {
                    val keep = state
                    state = newState
                    delay(1.seconds)
                    if (!pop) state = keep
                    if (pop) navigator.pop()
                }
            }

            Anvil(
                title = UI_HOME_EDITOR_RENAME_TITLE.replace("<name>", home.name),
                text = home.name,
                left = itemStack(Material.YELLOW_STAINED_GLASS_PANE) {
                    lore(UI_HOME_EDITOR_RENAME_EXIT_LORE)
                },
                right = itemStack(Material.GRAY_STAINED_GLASS_PANE) {
                    meta {
                        isHideTooltip = true
                    }
                },
                output = itemStack(Material.PAPER) {
                    lore(
                        when (state) {
                            EDITING -> UI_HOME_EDITOR_RENAME_SAVE_EDITING(home)
                            INVALID -> UI_HOME_EDITOR_RENAME_SAVE_INVALID_LORE
                            TOO_LONG -> UI_HOME_EDITOR_RENAME_SAVE_TOO_LONG
                            EXISTED -> UI_HOME_EDITOR_RENAME_SAVE_EXISTED
                            SUCCEED -> UI_HOME_EDITOR_RENAME_SAVED
                        }
                    )
                    meta {
                        setEnchantmentGlintOverride(
                            when (state) {
                                EDITING -> false
                                else -> true
                            }
                        )
                    }
                },
                onClick = { s, r ->
                    when (s) {
                        INPUT_LEFT -> {
                            navigator.pop()
                            listOf()
                        }

                        OUTPUT -> {
                            if (state != EDITING) return@Anvil listOf()
                            val input = r.text

                            if (!input.isValidIdentifier) {
                                player.playSound(UI_HOME_EDITOR_RENAME_INVALID_SOUND)
                                stateTransition(INVALID)
                                return@Anvil listOf()
                            }

                            if (input.length > manager.nameLengthLimit) {
                                player.playSound(UI_HOME_EDITOR_RENAME_INVALID_SOUND)
                                stateTransition(TOO_LONG)
                                return@Anvil listOf()
                            }

                            coroutineScope.launch {
                                if (manager.has(player, input)) {
                                    player.playSound(UI_HOME_EDITOR_RENAME_INVALID_SOUND)
                                    stateTransition(EXISTED)
                                    return@launch
                                }

                                submitAsync {
                                    home.name = input
                                    home.update()
                                }

                                stateTransition(SUCCEED, true)
                                player.playSound(UI_HOME_EDIT_SUCCEED_SOUND)
                            }

                            listOf()
                        }

                        else -> listOf()
                    }
                }
            )
        }
    }

    private enum class PreferState {
        NOT_PREFERRED, PRRFERRED, SUCCEED
    }

    @Composable
    @Suppress("FunctionName")
    private fun Prefer() {
        val player = LocalPlayer.current
        val coroutineScope = rememberCoroutineScope()
        var state by remember {
            mutableStateOf(if (!home.isPreferred) NOT_PREFERRED else PRRFERRED)
        }

        fun stateTransition(newState: PreferState) {
            coroutineScope.launch {
                state = PreferState.SUCCEED
                delay(1.seconds)
                state = newState
            }
            player.playSound(UI_HOME_EDIT_SUCCEED_SOUND)
        }

        Item(
            material = Material.SUNFLOWER,
            name = when (state) {
                NOT_PREFERRED -> UI_HOME_PREFER
                PRRFERRED -> UI_HOME_PREFER_UNSET
                PreferState.SUCCEED -> UI_HOME_EDIT_SUCCEED
            },
            lore = when (state) {
                NOT_PREFERRED -> UI_HOME_PREFER_LORE
                PRRFERRED -> UI_HOME_PREFER_UNSET_LORE
                PreferState.SUCCEED -> listOf()
            },
            enchantmentGlint = state == PreferState.SUCCEED || state == PRRFERRED,
            modifier = Modifier.clickable {
                if (!home.isLoaded) return@clickable
                if (state == PreferState.SUCCEED) return@clickable
                if (clickType != ClickType.LEFT) return@clickable

                if (home.isPreferred) {
                    submitAsync { home.setPreferred(false) }
                    stateTransition(NOT_PREFERRED)
                    return@clickable
                }

                submitAsync { home.setPreferred(true) }
                stateTransition(PRRFERRED)
            }
        )
    }

    private enum class StarState {
        NOT_STARRED, STARRED, SUCCEED
    }

    @Composable
    @Suppress("FunctionName")
    private fun Star() {
        val player = LocalPlayer.current
        val coroutineScope = rememberCoroutineScope()
        var state by remember {
            mutableStateOf(if (!home.isStarred) NOT_STARRED else STARRED)
        }

        fun stateTransition(newState: StarState) {
            coroutineScope.launch {
                state = StarState.SUCCEED
                delay(1.seconds)
                state = newState
            }
            player.playSound(UI_HOME_EDIT_SUCCEED_SOUND)
        }

        Item(
            material = Material.NETHER_STAR,
            name = when (state) {
                NOT_STARRED -> UI_HOME_STAR
                STARRED -> UI_HOME_STAR_UNSET
                StarState.SUCCEED -> UI_HOME_EDIT_SUCCEED
            },
            lore = when (state) {
                NOT_STARRED -> UI_HOME_STAR_LORE
                STARRED -> UI_HOME_STAR_UNSET_LORE
                StarState.SUCCEED -> listOf()
            },
            enchantmentGlint = state == StarState.SUCCEED || state == STARRED,
            modifier = Modifier.clickable {
                if (!home.isLoaded) return@clickable
                if (state == StarState.SUCCEED) return@clickable
                if (clickType != ClickType.LEFT) return@clickable

                if (home.isStarred) {
                    submitAsync {
                        home.isStarred = false
                        home.update()
                    }
                    stateTransition(NOT_STARRED)
                    return@clickable
                }

                submitAsync {
                    home.isStarred = true
                    home.update()
                }
                stateTransition(STARRED)
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Rename() {
        val navigator = LocalNavigator.currentOrThrow
        val succeed by remember { mutableStateOf(false) }
        Item(
            material = Material.NAME_TAG,
            name = if (!succeed) UI_HOME_RENAME else UI_HOME_EDIT_SUCCEED,
            lore = if (!succeed) UI_HOME_RENAME_LORE else listOf(),
            enchantmentGlint = succeed,
            modifier = Modifier.clickable {
                if (!home.isLoaded) return@clickable
                if (clickType != ClickType.LEFT) return@clickable
                if (succeed) return@clickable
                navigator.push(RenameScreen())
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun ChangeLocation() {
        val player = LocalPlayer.current
        var succeed by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        Item(
            material = Material.COMPASS,
            name = if (!succeed) UI_HOME_CHANGE_LOCATION else UI_HOME_EDIT_SUCCEED,
            lore = if (!succeed) UI_HOME_CHANGE_LOCATION_LORE else listOf(),
            enchantmentGlint = succeed,
            modifier = Modifier.clickable {
                if (!home.isLoaded) return@clickable
                if (clickType != ClickType.LEFT || succeed) return@clickable
                home.location = player.location
                submitAsync { home.update() }
                whoClicked.playSound(UI_HOME_EDIT_SUCCEED_SOUND)
                coroutineScope.launch {
                    succeed = true
                    delay(1.seconds)
                    succeed = false
                }
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Delete() {
        val manager = koinInject<HomeManager>()
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        Item(
            material = Material.RED_STAINED_GLASS_PANE,
            name = UI_HOME_DELETE,
            lore = UI_HOME_DELETE_LORE,
            modifier = Modifier.clickable {
                if (!home.isLoaded) return@clickable
                if (clickType != ClickType.SHIFT_LEFT) return@clickable
                coroutineScope.launch {
                    manager.remove(home.id)
                    whoClicked.playSound(UI_HOME_EDITOR_REMOVE_SOUND)
                    navigator.pop()
                }
            }
        )
    }

}