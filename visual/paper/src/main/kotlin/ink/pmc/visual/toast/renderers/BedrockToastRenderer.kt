package ink.pmc.visual.toast.renderers

import ink.pmc.utils.bedrock.useBedrockColors
import ink.pmc.visual.api.toast.Toast
import ink.pmc.visual.api.toast.dsl.toast
import org.bukkit.entity.Player

class BedrockToastRenderer : NmsToastRenderer() {

    override fun render(player: Player, obj: Toast) {
        super.render(player, toast {
            icon(obj.icon)
            message(obj.message.useBedrockColors())
            type(obj.type)
            frame(obj.frame)
        })
    }

}