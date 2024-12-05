package ink.pmc.framework

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import ink.pmc.framework.interactive.GuiManager
import org.bukkit.entity.Player

inline fun Player.startInventory(crossinline content: @Composable () -> Unit) {
    GuiManager.startInventory(this) {
        content()
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Player.startScreen(screen: Screen) {
    GuiManager.startScreen(this, screen)
}