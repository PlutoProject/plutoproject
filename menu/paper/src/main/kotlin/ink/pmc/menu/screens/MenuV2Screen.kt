package ink.pmc.menu.screens

import androidx.compose.runtime.Composable
import ink.pmc.framework.interactive.InteractiveScreen
import ink.pmc.menu.MenuConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MenuV2Screen : InteractiveScreen(), KoinComponent {
    private val config by inject<MenuConfig>()

    @Composable
    override fun Content() {
    }
}