package ink.pmc.visual.display.text

import ink.pmc.visual.api.display.text.TextDisplay
import ink.pmc.visual.api.display.text.TextDisplayFactory
import ink.pmc.visual.api.display.text.TextDisplayOptions
import net.kyori.adventure.text.Component
import org.bukkit.Location

class TextDisplayFactoryImpl : TextDisplayFactory {

    override fun create(location: Location, contents: Collection<Component>, options: TextDisplayOptions): TextDisplay {
        return TextDisplayImpl(location, contents, options)
    }

}