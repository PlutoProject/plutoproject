package ink.pmc.visual.api.display

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

@Suppress("UNUSED")
interface DisplayView {

    val uuid: UUID
    val location: Location
    val viewer: Player
    val options: DisplayOptions

    fun update()

}