package ink.pmc.framework.interactive

import androidx.compose.runtime.Composable
import org.bukkit.Material

@Composable
@Suppress("FunctionName")
fun Empty(modifier: Modifier = Modifier) {
    Item(material = Material.AIR, modifier = modifier)
}

@Composable
@Suppress("FunctionName")
fun ItemEmpty() {
    Empty(modifier = Modifier.width(1).height(1))
}