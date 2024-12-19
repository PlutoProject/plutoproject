package ink.pmc.framework.visual.display.text

import ink.pmc.framework.inject.inlinedGet
import ink.pmc.framework.visual.display.DisplayManager
import org.bukkit.entity.Player

interface TextDisplayManager :
    DisplayManager<TextDisplay, TextDisplayView> {

    companion object : TextDisplayManager by inlinedGet()

    fun create(
        viewer: Player,
        display: TextDisplay,
        renderer: TextDisplayRenderer = DefaultTextDisplayRenderer
    ): TextDisplayView

}