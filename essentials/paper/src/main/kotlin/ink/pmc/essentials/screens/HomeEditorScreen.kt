package ink.pmc.essentials.screens

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.essentials.*
import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.interactive.inventory.components.Back
import ink.pmc.interactive.inventory.components.Item
import ink.pmc.interactive.inventory.components.Placeholder
import ink.pmc.interactive.inventory.components.canvases.Chest
import ink.pmc.interactive.inventory.layout.Box
import ink.pmc.interactive.inventory.layout.Column
import ink.pmc.interactive.inventory.layout.Row
import ink.pmc.interactive.inventory.modifiers.*
import ink.pmc.interactive.inventory.modifiers.click.clickable
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.submitAsync
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.seconds

class HomeEditorScreen(private val player: Player, private val home: Home) : Screen {

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

    @Composable
    @Suppress("FunctionName")
    private fun Rename() {
        Item(
            material = Material.NAME_TAG,
            name = UI_HOME_RENAME,
            lore = UI_HOME_RENAME_LORE,
            modifier = Modifier.clickable {
                if (!home.isLoaded) return@clickable
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