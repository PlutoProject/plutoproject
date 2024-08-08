package ink.pmc.essentials.screens

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.screens.HomeEditorScreen.State.*
import ink.pmc.interactive.inventory.canvas.Anvil
import ink.pmc.interactive.inventory.components.Back
import ink.pmc.interactive.inventory.components.Item
import ink.pmc.interactive.inventory.components.Placeholder
import ink.pmc.interactive.inventory.components.canvases.Chest
import ink.pmc.interactive.inventory.layout.Box
import ink.pmc.interactive.inventory.layout.Column
import ink.pmc.interactive.inventory.layout.Row
import ink.pmc.interactive.inventory.modifiers.*
import ink.pmc.interactive.inventory.modifiers.click.clickable
import ink.pmc.utils.chat.isValidIdentifier
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.dsl.itemStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.wesjd.anvilgui.AnvilGUI.Slot.INPUT_LEFT
import net.wesjd.anvilgui.AnvilGUI.Slot.OUTPUT
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.seconds

class HomeEditorScreen(private val player: Player, private val home: Home) : Screen {

    override val key: ScreenKey = "essentials_home_editor_${player.uniqueId}_${home.id}"

    @Composable
    override fun Content() {
        Chest(
            viewers = setOf(player),
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
            Rename()
            ChangeLocation()
            repeat(4) {
                Placeholder()
            }
            Delete()
        }
    }

    private enum class State {
        EDITING, INVALID, TOO_LONG, SUCCEED
    }

    inner class RenameScreen : Screen {
        override val key: ScreenKey = "essentials_home_editor_rename_${player.uniqueId}_${home.id}"

        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            var state by remember { mutableStateOf(EDITING) }
            val coroutineScope = rememberCoroutineScope()
            Anvil(
                viewer = player,
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
                            EDITING -> UI_HOME_EDITOR_RENAME_SAVE_EDITING
                            INVALID -> UI_HOME_EDITOR_RENAME_SAVE_INVALID_LORE
                            TOO_LONG -> UI_HOME_EDITOR_RENAME_SAVE_TOO_LONG
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
                onClick = { i, s ->
                    when (i) {
                        INPUT_LEFT -> {
                            navigator.pop()
                            listOf()
                        }

                        OUTPUT -> {
                            if (state != EDITING) return@Anvil listOf()
                            val input = s.text

                            if (!input.isValidIdentifier) {
                                player.playSound(UI_HOME_EDITOR_RENAME_INVALID_SOUND)
                                coroutineScope.launch {
                                    state = INVALID
                                    delay(1.seconds)
                                    state = EDITING
                                }
                                return@Anvil listOf()
                            }

                            if (input.length > Essentials.homeManager.nameLengthLimit) {
                                player.playSound(UI_HOME_EDITOR_RENAME_INVALID_SOUND)
                                coroutineScope.launch {
                                    state = TOO_LONG
                                    delay(1.seconds)
                                    state = EDITING
                                }
                                return@Anvil listOf()
                            }

                            submitAsync {
                                home.name = input
                                home.update()
                            }

                            coroutineScope.launch {
                                state = SUCCEED
                                delay(1.seconds)
                                navigator.pop()
                            }

                            player.playSound(UI_HOME_EDIT_SUCCEED_SOUND)
                            listOf()
                        }

                        else -> listOf()
                    }
                }
            )
        }
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
        Item(
            material = Material.RED_STAINED_GLASS_PANE,
            name = UI_HOME_DELETE,
            lore = UI_HOME_DELETE_LORE,
            modifier = Modifier.clickable {
                if (!home.isLoaded) return@clickable
                if (clickType != ClickType.SHIFT_LEFT) return@clickable
                submitAsync { manager.remove(home.id) }
                whoClicked.playSound(UI_HOME_EDITOR_REMOVE_SOUND)
                navigator.pop()
            }
        )
    }

}