package ink.pmc.essentials.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.essentials.*
import ink.pmc.interactive.inventory.components.Item
import ink.pmc.interactive.inventory.components.canvases.Chest
import ink.pmc.interactive.inventory.jetpack.Arrangement
import ink.pmc.interactive.inventory.layout.Box
import ink.pmc.interactive.inventory.layout.Column
import ink.pmc.interactive.inventory.layout.Row
import ink.pmc.interactive.inventory.modifiers.*
import ink.pmc.interactive.inventory.modifiers.click.clickable
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class HomeViewerScreen(
    private val player: Player,
    private val viewing: OfflinePlayer,
) : Screen {

    @Composable
    override fun Content() {
        val title by rememberSaveable { mutableStateOf(UI_HOME_LOADING) }
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

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        Border()
        HomePane()
        Border(false)
    }

    inner class HomePane(private val index: Int = 0) : Screen {
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            Row(modifier = Modifier.fillMaxWidth().height(3)) {
                Previous()
                HomeSection(index)
                Next()
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Previous() {
        val navigator = LocalNavigator.currentOrThrow
        Column(modifier = Modifier.fillMaxHeight().width(1)) {
            repeat(3) {
                Item(
                    material = Material.YELLOW_STAINED_GLASS_PANE,
                    name = UI_HOME_PREVIOUS,
                    modifier = Modifier.clickable {
                        navigator.pop()
                    }
                )
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Next() {
        val navigator = LocalNavigator.currentOrThrow
        Column(modifier = Modifier.fillMaxHeight().width(1)) {
            repeat(3) {
                Item(
                    material = Material.GREEN_STAINED_GLASS_PANE,
                    name = UI_HOME_NEXT,
                    modifier = Modifier.clickable {
                        navigator.pop()
                    }
                )
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun HomeSection(index: Int) {
        Column(modifier = Modifier.fillMaxHeight().width(7)) {
            Row(modifier = Modifier.fillMaxWidth().height(1)) {

            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun HomePane() {
        Navigator(HomePane(0))
    }

    @Composable
    @Suppress("FunctionName")
    private fun Placeholder() {
        Item(material = Material.GRAY_STAINED_GLASS_PANE, isHideTooltip = true)
    }

    @Composable
    @Suppress("FunctionName")
    private fun Close() {
        Item(
            material = Material.RED_STAINED_GLASS_PANE,
            name = UI_CLOSE,
            modifier = Modifier.clickable {
                player.closeInventory()
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Back() {
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
    private fun Border(navigation: Boolean = true) {
        Box(modifier = Modifier.fillMaxWidth().height(1)) {
            Row(modifier = Modifier.fillMaxSize()) {
                repeat(9) {
                    Placeholder()
                }
            }
            if (!navigation) return@Box
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Start) {
                val navigator = LocalNavigator.currentOrThrow
                Close()
                if (navigator.lastItemOrNull == null) return@Row
                Back()
            }
        }
    }

}