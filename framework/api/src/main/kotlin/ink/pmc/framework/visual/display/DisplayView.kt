package ink.pmc.framework.visual.display

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

@Suppress("UNUSED")
interface DisplayView {

    val uuid: UUID
    val location: Location
    val viewer: Player
    val renderer: DisplayRenderer<out DisplayView>
    val options: DisplayOptions

    fun render()

    fun destroy()

}