package ink.pmc.visual.display.text.renderers

import ink.pmc.utils.bedrock.useBedrockColors
import ink.pmc.visual.api.display.text.TextDisplayView
import ink.pmc.visual.display.text.TextDisplayViewImpl
import org.bukkit.entity.Player

class BedrockTextDisplayRenderer : NmsTextDisplayRenderer() {

    override fun spawn(viewer: Player, view: TextDisplayView) {
        super.spawn(
            viewer, TextDisplayViewImpl(
                uuid = view.uuid,
                options = view.options,
                renderer = view.renderer,
                contents = view.contents.map { it.useBedrockColors() },
                location = view.location,
                viewer = viewer
            )
        )
    }

}