package ink.pmc.transfer.lobby

import com.electronwill.nightconfig.core.Config
import org.bukkit.Location
import org.bukkit.World
import java.util.*

class PortalManager(private val config: Config, private val world: World) {

    private val views = mutableMapOf<UUID, PortalView>()
    val bounding: PortalBounding = createBounding()

    private fun createBounding(): PortalBounding {
        val a = Location(
            world,
            config.get("detect-a.x"),
            config.get("detect-a.y"),
            config.get("detect-a.z"),
        )

        val b = Location(
            world,
            config.get("detect-b.x"),
            config.get("detect-b.y"),
            config.get("detect-b.z"),
        )

        return PortalBounding(a, b)
    }

    fun refresh() {
        views.values.forEach {
            it.refresh()
        }
    }

}