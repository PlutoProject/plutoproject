package ink.pmc.framework.visual.display.text

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface TextDisplayFactory {

    companion object : TextDisplayFactory by object : KoinComponent {
        val instance by inject<TextDisplayFactory>()
    }.instance

    fun create(
        location: Location,
        contents: Collection<Component>,
        options: TextDisplayOptions
    ): TextDisplay

}