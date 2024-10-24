package ink.pmc.framework.visual.display

import com.google.common.collect.Multimap
import org.bukkit.entity.Player

interface DisplayManager<D : Display<V>, V : DisplayView> {

    val views: Multimap<Player, V>

    fun create(viewer: Player, display: D, renderer: DisplayRenderer<V>): V

    fun render(view: V)

    fun renderAll()

    fun destroy(view: V)

    fun destroyAll(player: Player)

    fun getViewing(player: Player): Collection<V>

}