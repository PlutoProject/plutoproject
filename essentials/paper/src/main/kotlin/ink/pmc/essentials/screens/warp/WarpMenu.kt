package ink.pmc.essentials.screens.warp

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import ink.pmc.framework.interactive.inventory.Item
import ink.pmc.framework.interactive.inventory.layout.Menu
import net.kyori.adventure.text.Component
import org.bukkit.Material

class WarpMenu : Screen {
    override val key: ScreenKey = "essentials_warp_menu"

    @Composable
    override fun Content() {
        Menu(title = Component.text("地标")) {
            repeat(21) {
                Item(material = Material.PAPER)
            }
        }
    }
}