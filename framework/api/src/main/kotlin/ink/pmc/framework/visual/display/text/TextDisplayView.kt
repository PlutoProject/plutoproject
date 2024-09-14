package ink.pmc.framework.visual.display.text

import ink.pmc.framework.visual.display.DisplayView
import net.kyori.adventure.text.Component

interface TextDisplayView : DisplayView {

    override val options: TextDisplayOptions
    override val renderer: TextDisplayRenderer
    val contents: Collection<Component>

}