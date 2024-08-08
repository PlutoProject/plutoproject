package ink.pmc.interactive.inventory.components

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.interactive.inventory.modifiers.Modifier
import ink.pmc.interactive.inventory.modifiers.click.clickable
import ink.pmc.utils.chat.UI_BACK
import org.bukkit.Material

@Composable
@Suppress("FunctionName")
fun Back() {
    val navigator = LocalNavigator.currentOrThrow
    Item(
        material = Material.YELLOW_STAINED_GLASS_PANE,
        name = UI_BACK,
        modifier = Modifier.clickable {
            navigator.pop()
        }
    )
}