package ink.pmc.visual.api.display.text

import ink.pmc.visual.api.display.Display
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface TextDisplay : Display<TextDisplayView> {

    override val options: TextDisplayOptions
    val contents: Collection<Component>

    fun show(viewer: Player, renderer: TextDisplayRenderer = DefaultTextDisplayRenderer): TextDisplayView

}