package ink.pmc.visual.api.display.text

import ink.pmc.visual.api.display.DisplayOptions
import ink.pmc.visual.api.display.DisplayView
import net.kyori.adventure.text.Component

interface TextDisplayView : DisplayView {

    override val options: TextDisplayOptions
    val contents: Collection<Component>

}