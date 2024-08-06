package ink.pmc.visual.api.display

import com.google.common.collect.Multimap
import org.bukkit.entity.Player

interface DisplayManager<D : Display<V>, V : DisplayView> {

    val views: Multimap<Player, DisplayView>

    fun create(viewer: Player, display: D, renderer: DisplayRenderer<V>): V

    fun update(view: V)

    fun updateAll()

    fun destroy(view: V)

    fun destroyAll(player: Player)

    fun getViewing(player: Player): Collection<V>

}