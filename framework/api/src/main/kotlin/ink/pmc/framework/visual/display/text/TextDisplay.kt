package ink.pmc.framework.visual.display.text

import ink.pmc.framework.visual.display.Display
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface TextDisplay : Display<TextDisplayView> {

    override val options: TextDisplayOptions
    val contents: Collection<Component>

    fun show(
        viewer: Player,
        renderer: TextDisplayRenderer = DefaultTextDisplayRenderer
    ): TextDisplayView

}