package ink.pmc.visual.api.display.text

import ink.pmc.visual.api.display.DisplayManager
import org.bukkit.entity.Player

interface TextDisplayManager : DisplayManager<TextDisplay, TextDisplayView> {

    fun create(
        viewer: Player,
        display: TextDisplay,
        renderer: TextDisplayRenderer = DefaultTextDisplayRenderer
    ): TextDisplayView

}