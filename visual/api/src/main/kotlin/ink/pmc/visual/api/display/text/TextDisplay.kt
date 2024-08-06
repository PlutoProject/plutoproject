package ink.pmc.visual.api.display.text

import ink.pmc.visual.api.display.Display
import net.kyori.adventure.text.Component

interface TextDisplay : Display<TextDisplayView> {

    val contents: Collection<Component>

}