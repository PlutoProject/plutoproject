package ink.pmc.essentials.screens.warp

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import ink.pmc.framework.interactive.LocalPlayer

class WarpMenu : Screen {
    override val key: ScreenKey = "essentials_warp_menu"

    @Composable
    override fun Content() {
        val player = LocalPlayer.current
    }
}