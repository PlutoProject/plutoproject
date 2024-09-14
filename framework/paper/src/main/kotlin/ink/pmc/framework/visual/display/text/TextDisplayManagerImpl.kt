package ink.pmc.framework.visual.display.text

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import ink.pmc.framework.visual.display.DisplayRenderer
import org.bukkit.entity.Player

class TextDisplayManagerImpl : TextDisplayManager {
    override val views: Multimap<Player, TextDisplayView> = Multimaps.synchronizedSetMultimap(HashMultimap.create())

    override fun create(viewer: Player, display: TextDisplay, renderer: TextDisplayRenderer): TextDisplayView {
        return create(viewer, display, renderer)
    }

    override fun create(
        viewer: Player,
        display: TextDisplay,
        renderer: DisplayRenderer<TextDisplayView>
    ): TextDisplayView {
        val view = TextDisplayViewImpl(
            uuid = display.uuid,
            options = display.options,
            renderer = renderer as TextDisplayRenderer,
            contents = display.contents,
            location = display.location,
            viewer = viewer
        )
        view.render()
        views.put(viewer, view)
        return view
    }

    override fun render(view: TextDisplayView) {
        view.render()
    }

    override fun renderAll() {
        views.forEach { _, it -> it.render() }
    }

    override fun destroy(view: TextDisplayView) {
        view.destroy()
        views.values().remove(view)
    }

    override fun destroyAll(player: Player) {
        getViewing(player).forEach { it.destroy() }
        views.removeAll(player)
    }

    override fun getViewing(player: Player): Collection<TextDisplayView> {
        return views.get(player)
    }
}