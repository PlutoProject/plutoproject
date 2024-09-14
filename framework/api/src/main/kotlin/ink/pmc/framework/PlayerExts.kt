package ink.pmc.framework

import androidx.compose.runtime.Composable
import ink.pmc.framework.interactive.GuiManager
import org.bukkit.entity.Player

inline fun Player.startInventory(crossinline content: @Composable () -> Unit) {
    GuiManager.startInventory(this) {
        content()
    }
}