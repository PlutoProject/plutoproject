package ink.pmc.interactive.api.gui.inventory.components

import androidx.compose.runtime.Composable
import org.bukkit.Material

@Composable
@Suppress("FunctionName")
fun Placeholder() {
    Item(
        material = Material.GRAY_STAINED_GLASS_PANE,
        isHideTooltip = true
    )
}