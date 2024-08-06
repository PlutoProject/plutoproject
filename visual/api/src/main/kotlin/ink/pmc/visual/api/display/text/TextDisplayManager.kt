package ink.pmc.visual.api.display.text

import ink.pmc.visual.api.display.DisplayManager
import org.bukkit.entity.Player

interface TextDisplayManager : DisplayManager<TextDisplay, TextDisplayView> {

    fun create(display: TextDisplay, viewer: Player, renderer: TextDisplayRenderer = DefaultTextDisplayRenderer)

}