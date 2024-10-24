package ink.pmc.framework.visual.display

import org.bukkit.entity.Player

interface DisplayRenderer<V : DisplayView> {

    fun render(viewer: Player, view: V)

    fun spawn(viewer: Player, view: V)

    fun remove(viewer: Player, view: V)

}