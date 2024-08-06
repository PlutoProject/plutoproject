package ink.pmc.visual.display.text

import ink.pmc.visual.api.display.text.TextDisplayOptions
import ink.pmc.visual.api.display.text.TextDisplayRenderer
import ink.pmc.visual.api.display.text.TextDisplayView
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class TextDisplayViewImpl(
    override val uuid: UUID,
    override val options: TextDisplayOptions,
    override val renderer: TextDisplayRenderer,
    override val contents: Collection<Component>,
    override val location: Location,
    override val viewer: Player
) : TextDisplayView {

    override fun render() {
        renderer.render(viewer, this)
    }

    override fun destroy() {
        renderer.remove(viewer, this)
    }

}