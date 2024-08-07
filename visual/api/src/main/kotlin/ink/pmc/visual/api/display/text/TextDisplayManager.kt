package ink.pmc.visual.api.display.text

import ink.pmc.visual.api.display.DisplayManager
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface TextDisplayManager : DisplayManager<TextDisplay, TextDisplayView> {

    companion object : TextDisplayManager by object : KoinComponent {
        val instance by inject<TextDisplayManager>()
    }.instance

    fun create(
        viewer: Player,
        display: TextDisplay,
        renderer: TextDisplayRenderer = DefaultTextDisplayRenderer
    ): TextDisplayView

}