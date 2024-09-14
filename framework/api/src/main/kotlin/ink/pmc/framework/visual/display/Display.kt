package ink.pmc.framework.visual.display

import ink.pmc.framework.visual.display.text.TextDisplayView
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

interface Display<V : DisplayView> {

    val uuid: UUID
    val location: Location
    val options: DisplayOptions

    fun show(viewer: Player, renderer: DisplayRenderer<V>): TextDisplayView

}