package ink.pmc.framework.visual.display.text

import net.kyori.adventure.text.Component
import org.bukkit.Location

class TextDisplayFactoryImpl : TextDisplayFactory {
    override fun create(location: Location, contents: Collection<Component>, options: TextDisplayOptions): TextDisplay {
        return TextDisplayImpl(location, contents, options)
    }
}