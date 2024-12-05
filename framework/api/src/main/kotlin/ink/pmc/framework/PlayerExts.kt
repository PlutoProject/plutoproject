package ink.pmc.framework

import androidx.compose.runtime.Composable
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.interactive.InteractiveScreen
import org.bukkit.entity.Player

inline fun Player.startInventory(crossinline content: @Composable () -> Unit) {
    GuiManager.startInventory(this) {
        content()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Player.startScreen(screen: InteractiveScreen) {
    GuiManager.startScreen(this, screen)
}