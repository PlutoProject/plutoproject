package ink.pmc.interactive.api.inventory.components

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.framework.utils.chat.UI_BACK
import ink.pmc.framework.interactive.inventory.Modifier
import ink.pmc.framework.interactive.inventory.click.clickable
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