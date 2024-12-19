package ink.pmc.framework.interactive.lists

import androidx.compose.runtime.*
import ink.pmc.framework.interactive.*
import ink.pmc.framework.interactive.jetpack.Arrangement
import ink.pmc.framework.interactive.layout.Box
import ink.pmc.framework.interactive.layout.Column
import ink.pmc.framework.interactive.layout.Row
import ink.pmc.framework.interactive.layout.Size
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Composable
fun <T> Paginated(
    items: List<T>,
    page: Int,
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
    val itemsPerPage = size.width * size.height
    Box(Modifier.fillMaxSize()) {
        val start = page * itemsPerPage
        val end = (page + 1) * itemsPerPage
        val pageItems = remember(start, end) {
            if (start < 0) emptyList()
            else items.subList(start, end.coerceAtMost(items.size))
        }
        NavbarLayout(
            position = navbarPosition,
            navbar = {
                NavbarButtons(navbarPosition, navbarBackground) {
                    if (page > 0) previousButton()
                    else Spacer(1, 1)
                    if (end < items.size) nextButton()
                    else Spacer(1, 1)
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

@Composable
inline fun NavbarButtons(
    navbarPosition: NavbarPosition,
    background: ItemStack?,
    crossinline content: @Composable () -> Unit
) {
    val navbarSize =
        if (navbarPosition.isVertical()) Modifier.fillMaxHeight().width(1)
        else Modifier.fillMaxWidth().height(1)

    Box(modifier = navbarSize) {
        if (background != null)
            Item(background, modifier = Modifier.fillMaxSize())

        if (navbarPosition.isVertical())
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceAround) { content() }
        else
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) { content() }
    }
}


enum class NavbarPosition {
    START, END, TOP, BOTTOM;

    fun isVertical() = this == START || this == END
    fun isHorizontal() = this == TOP || this == BOTTOM
}

@Composable
fun NavbarLayout(
    position: NavbarPosition,
    navbar: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    when (position) {
        NavbarPosition.START ->
            Row {
                navbar()
                content()
            }

        NavbarPosition.END ->
            Row {
                content()
                navbar()
            }

        NavbarPosition.TOP ->
            Column {
                navbar()
                content()
            }

        NavbarPosition.BOTTOM ->
            Column {
                content()
                navbar()
            }
    }
}
