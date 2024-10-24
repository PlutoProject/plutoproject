package ink.pmc.interactive.api.inventory.components.lists

import androidx.compose.runtime.*
import ink.pmc.framework.interactive.inventory.layout.Box
import ink.pmc.framework.interactive.inventory.layout.Size
import ink.pmc.interactive.api.inventory.modifiers.Modifier
import ink.pmc.interactive.api.inventory.modifiers.fillMaxSize
import ink.pmc.interactive.api.inventory.modifiers.onSizeChanged
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Composable
fun <T> Scrollable(
    items: List<T>,
    startLine: Int,
    itemsPerLine: Int,
    totalLines: Int,
    nextButton: @Composable () -> Unit,
    previousButton: @Composable () -> Unit,
    navbarPosition: NavbarPosition = NavbarPosition.BOTTOM,
    navbarBackground: ItemStack? = remember {
        ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
            editMeta {
                it.isHideTooltip = true
            }
        }
    },
    content: @Composable (page: List<T>) -> Unit,
) {
    var size by remember { mutableStateOf(Size(0, 0)) }
    Box(Modifier.fillMaxSize()) {
        val start = startLine * itemsPerLine
        val end = (startLine + 1) * itemsPerLine * totalLines
        val pageItems = remember(start, end) {
            if (start < 0) emptyList()
            else items.subList(start, end.coerceAtMost(items.size))
        }
        NavbarLayout(
            position = navbarPosition,
            navbar = {
                NavbarButtons(navbarPosition, navbarBackground) {
                    if (startLine > 0) previousButton()
                    //else Spacer(1, 1)
                    if (end < items.size) nextButton()
                    //else Spacer(1, 1)
                }
            },
            content = {
                Box(Modifier.fillMaxSize().onSizeChanged {
                    size = it
                }) {
                    content(pageItems)
                }
            }
        )
    }
}
