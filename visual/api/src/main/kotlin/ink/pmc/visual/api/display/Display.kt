package ink.pmc.visual.api.display

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID

interface Display<V : DisplayView> {

    val uuid: UUID
    val renderer: DisplayRenderer<V>
    val location: Location
    val options: DisplayOptions

    fun show(viewer: Player, renderer: DisplayRenderer<V>)

}