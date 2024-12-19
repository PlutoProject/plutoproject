package ink.pmc.framework.interactive

import androidx.compose.runtime.Composable
import org.bukkit.Material

@Composable
@Suppress("FunctionName")
fun Placeholder(modifier: Modifier = Modifier) {
    Item(
        material = Material.GRAY_STAINED_GLASS_PANE,
        isHideTooltip = true,
        modifier = modifier
    )
}