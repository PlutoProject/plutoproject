package ink.pmc.framework.visual.display.text

import ink.pmc.framework.visual.display.DisplayRenderer
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class TextDisplayImpl(
    override val location: Location,
    override val contents: Collection<Component>,
    override val options: TextDisplayOptions
) : TextDisplay, KoinComponent {
    private val manager by inject<TextDisplayManager>()

    override val uuid: UUID = UUID.randomUUID()

    override fun show(viewer: Player, renderer: TextDisplayRenderer): TextDisplayView {
        return show(viewer, renderer)
    }

    override fun show(viewer: Player, renderer: DisplayRenderer<TextDisplayView>): TextDisplayView {
        return manager.create(viewer, this, renderer)
    }
}