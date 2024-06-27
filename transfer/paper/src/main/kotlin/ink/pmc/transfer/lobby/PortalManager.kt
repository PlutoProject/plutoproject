package ink.pmc.transfer.lobby

import com.electronwill.nightconfig.core.Config
import org.bukkit.Axis
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

class PortalManager(private val config: Config, private val world: World) {

    private val views = mutableMapOf<Player, PortalView>()
    val bounding: PortalBounding = createBounding()
    private val meta = createMeta()

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

    private fun createMeta(): PortalMeta {
        val a = Location(
            world,
            config.get("a.x"),
            config.get("a.y"),
            config.get("a.z"),
        )

        val b = Location(
            world,
            config.get("b.x"),
            config.get("b.y"),
            config.get("b.z"),
        )

        return PortalMeta(a, b, getAxis())
    }

    private fun getAxis(): Axis {
        return when (config.get<String>("axis")) {
            "x" -> Axis.X
            "z" -> Axis.Z
            else -> throw IllegalArgumentException("Invalid axis")
        }
    }

    fun hasView(player: Player): Boolean {
        return views.containsKey(player)
    }

    fun createView(player: Player) {
        if (hasView(player)) {
            return
        }

        val view = PortalView(player, meta)
        view.update()
        views[player] = view
    }

    fun destroyView(player: Player) {
        views[player]?.destroy()
        views.remove(player)
    }

    fun update() {
        views.values.forEach {
            it.update()
        }
    }

    fun destroyAll() {
        views.values.forEach {
            it.destroy()
        }
        views.clear()
    }

}